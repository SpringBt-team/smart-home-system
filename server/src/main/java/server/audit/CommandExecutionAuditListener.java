package server.audit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import server.executions.CommandExecutedEvent;

@Component
public class CommandExecutionAuditListener {
    
    private static final Logger log = LoggerFactory.getLogger(CommandExecutionAuditListener.class);

    @ApplicationModuleListener
    public void onCommandExecuted(CommandExecutedEvent event) {
        log.info("Audit log: command execution id={} finished with status={}",
                event.executionId(), event.status());
    }
}