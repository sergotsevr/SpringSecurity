package security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import security.model.User;


import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String email);
}
