package ru.practicum.shareit.user;

import org.junit.jupiter.api.function.Executable;
import ru.practicum.shareit.exceptions.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserAssertions {

    public static void assertUserNotFound(long id, Executable executable) {
        NotFoundException exception =
                assertThrows(NotFoundException.class, executable);
        assertEquals("Unable to find the user #" + id, exception.getMessage());
    }

}
