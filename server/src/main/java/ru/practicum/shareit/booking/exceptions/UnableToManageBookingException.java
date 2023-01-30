package ru.practicum.shareit.booking.exceptions;

public class UnableToManageBookingException extends RuntimeException {
    public UnableToManageBookingException(String message) {
        super(message);
    }
}
