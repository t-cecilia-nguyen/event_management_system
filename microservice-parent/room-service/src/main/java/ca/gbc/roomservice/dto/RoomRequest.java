package ca.gbc.roomservice.dto;

public record RoomRequest(
        String roomName,
        int capacity,
        String features,
        boolean availability
        ) { }
