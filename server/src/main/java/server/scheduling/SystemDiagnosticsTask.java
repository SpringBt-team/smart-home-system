package server.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

@Component
public class SystemDiagnosticsTask {

    private static final Logger log = LoggerFactory.getLogger(SystemDiagnosticsTask.class);

    @Scheduled(initialDelayString = "${smarthome.scheduling.diagnostics.initial-delay:PT30S}",
            fixedDelayString = "${smarthome.scheduling.diagnostics.delay:PT60S}")
    public void logSystemDiagnostics() {
        Runtime runtime = Runtime.getRuntime();
        long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMb = runtime.maxMemory() / (1024 * 1024);
        int threads = ManagementFactory.getThreadMXBean().getThreadCount();
        log.info("Діагностика вузла: пам'ять {}/{} МБ, потоків {}, виконує потік {}",
                usedMb, maxMb, threads, Thread.currentThread().getName());
    }
}
