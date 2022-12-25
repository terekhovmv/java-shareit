package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto book(
            @RequestHeader("X-Sharer-User-Id") long callerId,
            @Valid @RequestBody BookingRequestDto bookingRequestDto
    ) {
        return service.book(callerId, bookingRequestDto);
    }

    @PatchMapping("/{id}")
    public BookingDto setApproved(
            @RequestHeader("X-Sharer-User-Id") long callerId,
            @PathVariable long id,
            @RequestParam("approved") boolean value
    ) {
        return service.setApproved(callerId, id, value);
    }

    @GetMapping("/{id}")
    public BookingDto findById(
            @RequestHeader("X-Sharer-User-Id") long callerId,
            @PathVariable long id
    ) {
        return service.findById(callerId, id);
    }

    @GetMapping
    public List<BookingDto> getCreated(
            @RequestHeader("X-Sharer-User-Id") long callerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return service.getCreated(callerId, parseBookingFilter(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getForOwnedItems(
            @RequestHeader("X-Sharer-User-Id") long callerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return service.getForOwnedItems(callerId, parseBookingFilter(state));
    }

    private BookingFilter parseBookingFilter(String value) {
        try {
            return BookingFilter.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Unknown state: " + value);
        }
    }
}
