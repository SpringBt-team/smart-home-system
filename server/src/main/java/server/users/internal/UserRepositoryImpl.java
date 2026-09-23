package server.users.internal;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
class UserRepositoryImpl implements UserRepository{

    private final Map<UUID, User> usersById = new ConcurrentHashMap<>();

    @Override
    public User save(User user){
        usersById.put(user.id, user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id){
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email){
        return usersById.values().stream().filter(u -> u.email.equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email){
        return findByEmail(email).isPresent();
    }
}
