package server.auth.internal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.auth.InvalidCredentialsException;
import server.auth.IssuedToken;
import server.users.UserAccount;
import server.users.UserService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserService userService;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userService);
    }

    @Test
    void test01() {
        UUID userId = UUID.randomUUID();
        UserAccount account = new UserAccount(userId, "olena@example.com", "Олена", Instant.now());
        when(userService.authenticate("olena@example.com", "qwerty123")).thenReturn(Optional.of(account));
        IssuedToken token = authService.login("olena@example.com", "qwerty123");
        assertNotNull(token.token());
        assertEquals(userId, token.userId());
    }

    @Test
    void test02() {
        when(userService.authenticate("olena@example.com", "wrong")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class,
                () -> authService.login("olena@example.com", "wrong"));
    }
}
