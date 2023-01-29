package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import java.time.LocalDateTime;

public class BookingServiceTestHelper {
    private final BookingService service;

    public BookingServiceTestHelper(BookingService service) {
        this.service = service;
    }

    public static BookingUpdateDto makeBookingUpdateDto(long itemId, LocalDateTime start, LocalDateTime end) {
        BookingUpdateDto result = new BookingUpdateDto();
        result.setItemId(itemId);
        result.setStart(start);
        result.setEnd(end);
        return result;
    }

    public static BookingUpdateDto makeBookingUpdateDto(long itemId) {
        return makeBookingUpdateDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
    }

    public BookingDto create(long callerId, long itemId, LocalDateTime start, LocalDateTime end) {
        return service.create(callerId, makeBookingUpdateDto(itemId, start, end));
    }

    public BookingDto create(long callerId, long itemId) {
        return service.create(callerId, makeBookingUpdateDto(itemId));
    }

    public long createAndGetId(long callerId, long itemId, LocalDateTime start, LocalDateTime end) {
        return create(callerId, itemId, start, end).getId();
    }

    public long createAndGetId(long callerId, long itemId) {
        return create(callerId, itemId).getId();
    }
}