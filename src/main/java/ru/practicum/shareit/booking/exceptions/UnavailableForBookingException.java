package ru.practicum.shareit.booking.exceptions;

public class UnavailableForBookingException extends RuntimeException {
    public UnavailableForBookingException(String message) {
        super(message);
    }
}
