package ca.gbc.userservice.dto;

import ca.gbc.userservice.model.Role;
import ca.gbc.userservice.model.UserType;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        UserType userType
) {}
