package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public BookingDto toBookingDto(Booking from) {
        return BookingDto.builder()
                .id(from.getId())
                .start(from.getStart())
                .end(from.getEnd())
                .itemId(from.getItemId())
                .bookerId(from.getBookerId())
                .status(from.getStatus())
                .build();
    }

    public Booking toBooking(BookingDto from) {
        return Booking.builder()
                .id(from.getId())
                .start(from.getStart())
                .end(from.getEnd())
                .itemId(from.getItemId())
                .bookerId(from.getBookerId())
                .status(from.getStatus())
                .build();
    }
}
