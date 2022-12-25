package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
    BookingDto book(long callerId, BookingRequestDto bookingRequestDto);
}
