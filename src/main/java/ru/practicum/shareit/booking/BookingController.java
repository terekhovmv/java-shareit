package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;

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
}
