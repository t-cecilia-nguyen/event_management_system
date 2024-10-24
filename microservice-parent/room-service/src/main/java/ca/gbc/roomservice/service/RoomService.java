package ca.gbc.roomservice.service;

import ca.gbc.roomservice.dto.RoomRequest;
import ca.gbc.roomservice.dto.RoomResponse;
import ca.gbc.roomservice.model.Room;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom (RoomRequest roomRequest);
    List<Room> getAllRooms();
    Room getRoomById(Long id);
    Room updateRoomAvailability(Long id, boolean availability);
    void deleteRoom(Long id);
    List<Room> checkRoomAvailability(boolean availability);
}
