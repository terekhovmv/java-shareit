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

import java.time.LocalDateTime;

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

    @Test
    void getFinishedCount() {
        User owner = userRepository.save(
                new User(null, "owner", "owner@abc.def")
        );
        User booker = userRepository.save(
                new User(null, "booker", "booker@abc.def")
        );
        User otherBooker = userRepository.save(
                new User(null, "other-booker", "other-booker@abc.def")
        );
        Item item = itemRepository.save(
                new Item(null, "item", "item-description", true, owner, null)
        );
        Item otherItem = itemRepository.save(
                new Item(null, "other-item", "other-item-description", true, owner, null)
        );

        addBooking(-1, item, booker, BookingStatus.APPROVED); //suitable
        addBooking(-1, item, otherBooker, BookingStatus.APPROVED); //booker mismatch
        addBooking(1, item, otherBooker, BookingStatus.APPROVED); //end date mismatch
        addBooking(-1, item, booker, BookingStatus.REJECTED); //status mismath
        addBooking(-1, otherItem, booker, BookingStatus.APPROVED); //item mismatch
        addBooking(-2, item, booker, BookingStatus.APPROVED); //suitable

        assertEquals(
                2,
                testee.getFinishedCount(booker.getId(), item.getId(), LocalDateTime.now())
        );
    }

    private Booking addBooking(int endDays, Item item, User booker, BookingStatus status) {
        return testee.save(
                new Booking(
                        null,
                        LocalDateTime.now().plusDays(endDays - 1000),
                        LocalDateTime.now().plusDays(endDays),
                        item,
                        booker,
                        status
                )
        );
    }

}
