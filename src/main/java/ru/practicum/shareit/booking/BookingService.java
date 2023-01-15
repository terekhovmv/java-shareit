package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.pagination.RandomAccessParams;

import java.util.List;

public interface BookingService {
    BookingDto create(long callerId, BookingUpdateDto dto);

    BookingDto setApproved(long callerId, long id, boolean value);

    BookingDto get(long callerId, long id);

    List<BookingDto> getCreated(long creatorId, BookingFilter filter, RandomAccessParams randomAccessParams);

    List<BookingDto> getForOwnedItems(long ownerId, BookingFilter filter, RandomAccessParams randomAccessParams);
}
