package ru.practicum.shareit.booking.exceptions;

public class AlreadyApprovedBookingException extends RuntimeException {
    public AlreadyApprovedBookingException(String message) {
        super(message);
    }
}
