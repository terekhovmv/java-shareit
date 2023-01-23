package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.BookingAssertions.assertBookingNotFound;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository testee;

    @Test
    void requireForExistent() {
        User owner = userRepository.save(
                new User(null, "owner", "owner@abc.def")
        );
        User booker = userRepository.save(
                new User(null, "booker", "booker@abc.def")
        );
        Item item = itemRepository.save(
                new Item(null, "name", "description", true, owner, null)
        );
        Booking created = testee.save(
                new Booking(null, null, null, item, booker, BookingStatus.WAITING)
        );

        assertEquals(
                created,
                testee.require(created.getId())
        );
    }

    @Test
    void requireForAbsent() {
        assertBookingNotFound(
                Long.MAX_VALUE,
                () -> testee.require(Long.MAX_VALUE)
        );
    }
}
