package ca.gbc.roomservice.service;

import ca.gbc.roomservice.dto.RoomRequest;
import ca.gbc.roomservice.dto.RoomResponse;
import ca.gbc.roomservice.model.Room;
import ca.gbc.roomservice.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);


    @Override
    public RoomResponse createRoom (RoomRequest roomRequest) {
        log.debug("Creating a new room {}", roomRequest.roomName());

        Room room = Room.builder()
                .roomName(roomRequest.roomName())
                .capacity(roomRequest.capacity())
                .features(roomRequest.features())
                .availability(roomRequest.availability())
                .build();

        roomRepository.save(room);

        log.info("Room {} is saved", room.getId());

        return new RoomResponse(room.getId(),
                room.getRoomName(),
                room.getCapacity(),
                room.getFeatures(),
                room.isAvailability());
    }
    @Override
    public List<RoomResponse> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        
        return rooms.stream().map(this::mapToRoomResponse).toList();
    }
    
    private RoomResponse mapToRoomResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomName(),
                room.getCapacity(),
                room.getFeatures(),
                room.isAvailability()
        );
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public String updateRoom(Long id, RoomRequest roomRequest) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Update
        room.setRoomName(roomRequest.roomName());
        room.setCapacity(roomRequest.capacity());
        room.setFeatures(roomRequest.features());
        room.setAvailability(roomRequest.availability());

        // Save
        Room updatedRoom = roomRepository.save(room);
        return updatedRoom.getId().toString();
    }

    @Override
    public void deleteRoom(Long id) {
        log.debug("Deleting room with id {}", id);
        roomRepository.deleteById(id);
    }

    @Override
    public List<Room> checkAllAvailability(boolean availability) {
        return roomRepository.findByAvailability(availability);
    }

    @Override
    public boolean checkRoomAvailability(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return room.isAvailability();
    }
}
