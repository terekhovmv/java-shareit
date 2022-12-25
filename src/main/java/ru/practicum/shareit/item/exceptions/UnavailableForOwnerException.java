package ru.practicum.shareit.item.exceptions;

public class UnavailableForOwnerException extends RuntimeException {
    public UnavailableForOwnerException(String message) {
        super(message);
    }
}
