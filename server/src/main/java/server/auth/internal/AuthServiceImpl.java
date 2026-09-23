package server.auth.internal;
import org.springframework.stereotype.Service;
import server.auth.AuthService;
import server.auth.InvalidCredentialsException;
import server.auth.IssuedToken;
import server.users.UserAccount;
import server.users.UserService;
import java.util.UUID;

@Service
class AuthServiceImpl implements AuthService{
    private final UserService userService;
    AuthServiceImpl(UserService userService){
        this.userService = userService;
    }

    @Override
    public IssuedToken login(String email, String rawPassword){
        UserAccount account = userService.authenticate(email, rawPassword).orElseThrow(InvalidCredentialsException::new);
         String token = UUID.randomUUID().toString();
         return new IssuedToken(token, account.id());
    }
}
