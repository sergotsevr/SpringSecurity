package security.sercive;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import security.model.SecurityUser;
import security.repository.UserRepo;

@Service("userDetailsServiceIml")
public class UserDetailsServiceIml implements UserDetailsService {

    private final UserRepo userRepo;

    public UserDetailsServiceIml(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return SecurityUser.buildFromCustomUser(userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User doesnt exists")));
    }


}
