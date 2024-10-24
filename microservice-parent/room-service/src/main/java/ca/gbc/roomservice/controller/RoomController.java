package ca.gbc.roomservice.controller;

import ca.gbc.roomservice.dto.RoomRequest;
import ca.gbc.roomservice.model.Room;
import ca.gbc.roomservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createRoom(@RequestBody RoomRequest roomRequest) {
        roomService.createRoom(roomRequest);
        return "Room created successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PutMapping("/{id}/availability")
    public Room updateRoomAvailability(@PathVariable Long id, @RequestParam boolean availability) {
        return roomService.updateRoomAvailability(id, availability);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/availability")
    public List<Room> checkRoomAvailability(@RequestParam boolean availability) {
        return roomService.checkRoomAvailability(availability);
    }

}
