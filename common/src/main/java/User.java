import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

@Entity
public class User {
    @Id
    UUID id;

    String email;
    String name;
    String passwordHash;
    Instant createdAt;
}
