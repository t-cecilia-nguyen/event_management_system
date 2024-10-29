package ca.gbc.roomservice.service;

import ca.gbc.roomservice.dto.RoomRequest;
import ca.gbc.roomservice.dto.RoomResponse;
import ca.gbc.roomservice.model.Room;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom (RoomRequest roomRequest);
    List<RoomResponse> getAllRooms();
    Room getRoomById(Long id);
    String updateRoom(Long id, RoomRequest roomRequest);
    void deleteRoom(Long id);
    List<Room> checkAllAvailability(boolean availability);
    boolean checkRoomAvailability(Long id);
}
