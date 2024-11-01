package ca.gbc.userservice.service;

import ca.gbc.userservice.dto.UserRequest;
import ca.gbc.userservice.model.Role;
import ca.gbc.userservice.model.User;
import ca.gbc.userservice.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    List<UserResponse> getAllUsers();
    User getUserById(Long id);
    void updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    List<User> checkAllRole(Role role);
    Role checkUserRole(Long id);
}
