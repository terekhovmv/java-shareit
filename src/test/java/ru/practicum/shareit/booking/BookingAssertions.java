package ru.practicum.shareit.booking;

import org.junit.jupiter.api.function.Executable;
import ru.practicum.shareit.exceptions.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingAssertions {

    public static void assertBookingNotFound(long id, Executable executable) {
        NotFoundException exception =
                assertThrows(NotFoundException.class, executable);
        assertEquals("Unable to find the booking #" + id, exception.getMessage());
    }

}