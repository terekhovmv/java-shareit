package ru.practicum.shareit.booking.exceptions;

public class UnableToCreateBookingException extends RuntimeException {
    public UnableToCreateBookingException(String message) {
        super(message);
    }
}
