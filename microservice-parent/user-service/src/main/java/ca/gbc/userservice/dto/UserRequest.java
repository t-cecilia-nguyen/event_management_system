package ca.gbc.userservice.dto;

public record UserRequest(
        String name,
        String email,
        String role,
        String userType
) {}
