package server.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabasePingTask {

    private static final Logger log = LoggerFactory.getLogger(DatabasePingTask.class);

    private final DataSource dataSource;

    public DatabasePingTask(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Scheduled(cron = "${smarthome.scheduling.db-ping.cron:0 */5 * * * *}", zone = "Europe/Kyiv")
    public void pingDatabase() {
        long start = System.nanoTime();
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(2);
            log.info("Перевірка БД: з'єднання {} за {} мс", valid ? "активне" : "НЕ активне",
                    (System.nanoTime() - start) / 1_000_000);
        } catch (SQLException e) {
            log.error("Перевірка БД: не вдалося отримати з'єднання", e);
        }
    }
}
