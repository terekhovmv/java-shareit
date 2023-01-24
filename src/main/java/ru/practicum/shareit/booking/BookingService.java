package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import java.util.List;

public interface BookingService {
    BookingDto create(long callerId, BookingUpdateDto dto);

    BookingDto setApproved(long callerId, long id, boolean value);

    BookingDto get(long callerId, long id);

    List<BookingDto> getCreated(long creatorId, BookingFilter filter, Pageable pageable);

    List<BookingDto> getForOwnedItems(long ownerId, BookingFilter filter, Pageable pageable);
}
