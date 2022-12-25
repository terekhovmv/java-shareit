package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto book(long callerId, BookingRequestDto bookingRequestDto);

    BookingDto setApproved(long callerId, long id, boolean value);

    BookingDto findById(long callerId, long id);

    List<BookingDto> getCreated(long callerId, BookingFilter filter);
}
