package ca.gbc.bookingservice.service;

import ca.gbc.bookingservice.client.RoomClient;
import ca.gbc.bookingservice.dto.BookingRequest;
import ca.gbc.bookingservice.dto.BookingResponse;
import ca.gbc.bookingservice.exception.ConflictException;
import ca.gbc.bookingservice.exception.RoomNotAvailableException;
import ca.gbc.bookingservice.model.Booking;
import ca.gbc.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final MongoTemplate mongoTemplate;
    private final RoomClient roomClient;

    @Override
    public BookingResponse createBooking(BookingRequest bookingRequest) {


        boolean isRoomAvailable = roomClient.checkRoomAvailability(bookingRequest.roomId());
        log.info("Room attempting to be booked has the availability status of: " + isRoomAvailable);

        log.debug("Creating a new booking for user {}", bookingRequest.userId());

        try {
            validateBooking(bookingRequest);
        } catch (ConflictException e) {
            log.error("Booking conflict while creating booking: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        if (isRoomAvailable) {

            Booking booking = Booking.builder()
                    .userId(bookingRequest.userId())
                    .roomId(bookingRequest.roomId())
                    .startTime(bookingRequest.startTime())
                    .endTime(bookingRequest.endTime())
                    .purpose(bookingRequest.purpose())
                    .build();

            bookingRepository.save(booking);
            log.info("Booking with ID {} created", booking.getId());
            return mapToBookingResponse(booking);
        } else {
            throw new RoomNotAvailableException("Room with ID " + bookingRequest.roomId() + " is not available.");
        }
    }

    @Override
    public List<BookingResponse> getAllBookings() {

        log.debug("Retrieving all bookings");

        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().map(this::mapToBookingResponse).toList();
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getRoomId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getPurpose()
        );
    }

    @Override
    public BookingResponse getBookingById(String bookingId) {

        log.debug("Retrieving booking with id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return mapToBookingResponse(booking);
    }

    @Override
    public String updateBooking(String bookingId, BookingRequest bookingRequest) {

        boolean isRoomAvailable = roomClient.checkRoomAvailability(bookingRequest.roomId());
        log.info("Room attempting to be booked has the availability status of: " + isRoomAvailable);

        log.debug("Updating booking with id {}", bookingId);

        try {
            validateBooking(bookingRequest);
        } catch (ConflictException e) {
            log.error("Booking conflict while creating booking: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        Query query = new Query(Criteria.where("id").is(bookingId));
        Booking booking = mongoTemplate.findOne(query, Booking.class);

        if (booking != null && isRoomAvailable) {
            booking.setUserId(bookingRequest.userId());
            booking.setRoomId(bookingRequest.roomId());
            booking.setStartTime(bookingRequest.startTime());
            booking.setEndTime(bookingRequest.endTime());
            booking.setPurpose(bookingRequest.purpose());
            bookingRepository.save(booking);

            log.info("Booking with id {} has been updated", bookingId);
            return bookingId;
        } else if (booking == null) {
            throw new RuntimeException("Booking could not be found");
        } else {
            throw new RoomNotAvailableException("Room with ID " + bookingRequest.roomId() + " is not available to be updated");
        }
    }

    @Override
    public void deleteBooking(String bookingId) {

        log.debug("Deleting booking with id {}", bookingId);

        if (!bookingRepository.existsById(bookingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        bookingRepository.deleteById(bookingId);
    }

    private void validateBooking(BookingRequest bookingRequest, String... bookingId) {

        log.debug("Validating booking for room {} between {} and {}",
                bookingRequest.roomId(),
                bookingRequest.startTime(),
                bookingRequest.endTime());

        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("roomId").is(bookingRequest.roomId()),
                        new Criteria().orOperator(
                                Criteria.where("startTime").lt(bookingRequest.endTime())
                                        .and("endTime").gt(bookingRequest.startTime())
                        )
                )
        );

        log.debug("Generated query: {}", query);

        if (bookingId != null && bookingId.length > 0) {
            query.addCriteria(Criteria.where("id").ne(bookingId[0]));
            log.debug("Excluding booking ID from validation: {}", bookingId[0]);
        }

        boolean hasOverlap = mongoTemplate.exists(query, Booking.class);
        log.debug("Has Overlap: {}", hasOverlap);

        if (hasOverlap) {
            throw new ConflictException("A booking already exists for the specified time range.");
        }
    }
}
