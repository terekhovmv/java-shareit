package ru.practicum.shareit.item;

import org.junit.jupiter.api.function.Executable;
import ru.practicum.shareit.exceptions.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemAssertions {

    public static void assertItemNotFound(long id, Executable executable) {
        NotFoundException exception =
                assertThrows(NotFoundException.class, executable);
        assertEquals("Unable to find the item #" + id, exception.getMessage());
    }

}
