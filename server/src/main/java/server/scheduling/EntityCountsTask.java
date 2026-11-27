package server.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import server.commands.CommandRepository;
import server.devices.DeviceRepository;
import server.executions.CommandExecutionRepository;

@Component
public class EntityCountsTask {

    private static final Logger log = LoggerFactory.getLogger(EntityCountsTask.class);

    private final DeviceRepository deviceRepository;
    private final CommandRepository commandRepository;
    private final CommandExecutionRepository executionRepository;

    public EntityCountsTask(DeviceRepository deviceRepository,
                            CommandRepository commandRepository,
                            CommandExecutionRepository executionRepository) {
        this.deviceRepository = deviceRepository;
        this.commandRepository = commandRepository;
        this.executionRepository = executionRepository;
    }

    @Scheduled(initialDelayString = "${smarthome.scheduling.entity-counts.initial-delay:PT20S}",
            fixedRateString = "${smarthome.scheduling.entity-counts.rate:PT30S}")
    public void logEntityCounts() {
        log.info("Лічильники: пристроїв={}, команд={}, виконань={}",
                deviceRepository.count(), commandRepository.count(), executionRepository.count());
    }
}
