package server.audit.internal;

import com.springbt.smarthome.common.audit.AuditAction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import server.audit.AuditLogEntity;

import java.time.Clock;
import java.time.Instant;

@Aspect
@Component
@Order(1)
public class AuditLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggingAspect.class);
    private static final String ANONYMOUS = "anonymous";
    private static final String HIDDEN = "[hidden]";
    private static final int MAX_ERROR_LENGTH = 1000;

    private final AuditLogWriter writer;
    private final Clock clock;

    public AuditLoggingAspect(AuditLogWriter writer, Clock clock) {
        this.writer = writer;
        this.clock = clock;
    }

    @Around("@annotation(auditAction)")
    public Object auditOperation(ProceedingJoinPoint joinPoint, AuditAction auditAction) throws Throwable {
        Instant performedAt = Instant.now(clock);
        long start = System.nanoTime();
        Object result = null;
        Throwable failure = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            failure = ex;
            throw ex;
        } finally {
            saveAuditRecord(joinPoint, auditAction, performedAt,
                    (System.nanoTime() - start) / 1_000_000, result, failure);
        }
    }

    private void saveAuditRecord(ProceedingJoinPoint joinPoint, AuditAction auditAction, Instant performedAt,
                                 long durationMs, Object result, Throwable failure) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            writer.write(new AuditLogEntity(
                    currentUsername(),
                    auditAction.action(),
                    signature.getDeclaringType().getSimpleName() + "." + signature.getName(),
                    auditAction.logParameters() ? AuditValues.formatAll(joinPoint.getArgs()) : HIDDEN,
                    signature.getReturnType() == void.class ? "void" : AuditValues.format(result),
                    failure == null,
                    failure == null ? null : failure.getClass().getName(),
                    failure == null ? null : AuditValues.truncate(failure.getMessage(), MAX_ERROR_LENGTH),
                    performedAt,
                    durationMs));
        } catch (RuntimeException e) {
            log.error("Не вдалося зберегти запис аудиту {}", auditAction.action(), e);
        }
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return ANONYMOUS;
        }
        return authentication.getName();
    }
}
