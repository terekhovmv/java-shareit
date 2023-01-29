package ru.practicum.shareit.gateway.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.ShareItAppConsts;
import ru.practicum.shareit.gateway.booking.dto.BookingFilter;
import ru.practicum.shareit.gateway.booking.dto.BookingUpdateDto;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient client;

    public BookingController(BookingClient client) {
        this.client = client;
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @Valid @RequestBody BookingUpdateDto dto
    ) {
        return client.create(callerId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> setApproved(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id,
            @RequestParam("approved") boolean value
    ) {
        return client.setApproved(callerId, id, value);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id
    ) {
        return client.get(callerId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getCreated(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "20") @Positive int size
    ) {
        requireValidBookingState(state);
        return client.getCreated(callerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getForOwnedItems(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "20") @Positive int size
    ) {
        requireValidBookingState(state);
        return client.getForOwnedItems(callerId, state, from, size);
    }

    private void requireValidBookingState(String value) {
        try {
            BookingFilter.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Unknown state: " + value);
        }
    }

/*
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }


 */
}
