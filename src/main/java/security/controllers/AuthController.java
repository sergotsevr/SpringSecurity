package security.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import security.model.AuthRequest;
import security.model.User;
import security.repository.UserRepo;
import security.sercive.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping()
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private UserRepo userRepo;
    private JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserRepo userRepo, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping ("/login")
    public ResponseEntity<?> getLoginPage(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            User user = userRepo.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("user doesn't exists"));
            String token = jwtTokenProvider.createToken(request.getUsername(), user.getRole().name());
            Map response = new HashMap();
            response.put("username", request.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        catch (AuthenticationException e){
            return new ResponseEntity("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }
}
