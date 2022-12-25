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
            @RequestParam(name = "state", defaultValue = "ALL") String state
    ) {
        BookingFilter filter;
        try {
            filter = BookingFilter.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Unknown state: " + state);
        }
        return service.getCreated(callerId, filter);
    }
}
