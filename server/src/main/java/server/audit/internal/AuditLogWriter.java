package server.audit.internal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import server.audit.AuditLogEntity;
import server.audit.AuditLogRepository;

@Component
public class AuditLogWriter {

    private final AuditLogRepository repository;

    public AuditLogWriter(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void write(AuditLogEntity entry) {
        repository.save(entry);
    }
}
