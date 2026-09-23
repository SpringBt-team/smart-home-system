package server.users;

import java.util.UUID;
import java.util.Optional;

public interface UserService {

    UserAccount register(String email, String rawPassword, String name);

    UserAccount findById(UUID id);

    Optional<UserAccount> authenticate(String name, String rawPassword);
}
