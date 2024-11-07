package ca.gbc.userservice.repository;

import ca.gbc.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserType(String userType);
}
