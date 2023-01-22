package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.exceptions.AlreadyApprovedBookingException;
import ru.practicum.shareit.booking.exceptions.UnableToCreateBookingException;
import ru.practicum.shareit.booking.exceptions.UnableToManageBookingException;
import ru.practicum.shareit.booking.exceptions.UnavailableForBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceTestHelper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceTestHelper;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.BookingAssertions.assertBookingNotFound;
import static ru.practicum.shareit.booking.BookingServiceTestHelper.makeBookingUpdateDto;
import static ru.practicum.shareit.user.UserAssertions.assertUserNotFound;

@Transactional
@SpringBootTest
public class BookingServiceImplTest {
    private static final long UNKNOWN_USER_ID = Long.MAX_VALUE;

    private static final long UNKNOWN_ITEM_ID = Long.MAX_VALUE;

    private static final long UNKNOWN_ID = Long.MAX_VALUE;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingServiceImpl testee;

    @Autowired
    private EntityManager em;

    private UserServiceTestHelper userHelper;

    private ItemServiceTestHelper itemHelper;

    private BookingServiceTestHelper bookingHelper;

    @BeforeEach
    void beforeEach() {
        if (userHelper == null) {
            userHelper = new UserServiceTestHelper(userService);
        }

        if (itemHelper == null) {
            itemHelper = new ItemServiceTestHelper(itemService);
        }

        if (bookingHelper == null) {
            bookingHelper = new BookingServiceTestHelper(testee);
        }
    }

    @Test
    void create() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);

        BookingUpdateDto dto = makeBookingUpdateDto(itemId);

        long bookerId = userHelper.createAndGetId("booker");
        BookingDto result = testee.create(bookerId, dto);

        Booking stored = em
                .createQuery(
                        "SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :bookerId",
                        Booking.class
                )
                .setParameter("itemId", itemId)
                .setParameter("bookerId", bookerId)
                .getSingleResult();
        assertEquals(dto.getStart(), stored.getStart());
        assertEquals(dto.getEnd(), stored.getEnd());

        assertEquals(stored.getId(), result.getId());
        assertEquals(dto.getStart(), result.getStart());
        assertEquals(dto.getEnd(), result.getEnd());
        assertEquals(itemId, result.getItem().getId());
        assertEquals(bookerId, result.getBooker().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void createByItemOwner() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);

        BookingUpdateDto dto = makeBookingUpdateDto(itemId);

        assertThrows(
                UnableToCreateBookingException.class,
                () -> testee.create(ownerId, dto)
        );
    }

    @Test
    void createForUnavailableItem() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", false, null);

        BookingUpdateDto dto = makeBookingUpdateDto(itemId);

        long bookerId = userHelper.createAndGetId("booker");

        assertThrows(
                UnavailableForBookingException.class,
                () -> testee.create(bookerId, dto)
        );
    }

    @Test
    void setApprovedTrue() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);
        long bookerId = userHelper.createAndGetId("booker");
        long bookingId = bookingHelper.createAndGetId(bookerId, itemId);

        BookingDto result = testee.setApproved(ownerId, bookingId, true);
        assertEquals(bookingId, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void setApprovedFalse() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);
        long bookerId = userHelper.createAndGetId("booker");
        long bookingId = bookingHelper.createAndGetId(bookerId, itemId);

        BookingDto result = testee.setApproved(ownerId, bookingId, false);
        assertEquals(bookingId, result.getId());
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void setApprovedByUnknownCaller() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);
        long bookerId = userHelper.createAndGetId("booker");
        long bookingId = bookingHelper.createAndGetId(bookerId, itemId);

        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.setApproved(UNKNOWN_USER_ID, bookingId, true)
        );
    }

    @Test
    void setApprovedByNotOwner() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);
        long bookerId = userHelper.createAndGetId("booker");
        long bookingId = bookingHelper.createAndGetId(bookerId, itemId);

        long callerId = userHelper.createAndGetId("caller");
        assertThrows(
                UnableToManageBookingException.class,
                () -> testee.setApproved(callerId, bookingId, true)
        );
    }

    @Test
    void setApprovedTrueAgain() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);
        long bookerId = userHelper.createAndGetId("booker");
        long bookingId = bookingHelper.createAndGetId(bookerId, itemId);

        testee.setApproved(ownerId, bookingId, true);
        assertThrows(
                AlreadyApprovedBookingException.class,
                () -> testee.setApproved(ownerId, bookingId, true)
        );
    }

    @Test
    void getById() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);
        long bookerId = userHelper.createAndGetId("booker");
        long bookingId = bookingHelper.createAndGetId(bookerId, itemId);

        BookingDto resultForOwner = testee.get(ownerId, bookingId);
        BookingDto resultForBooker = testee.get(ownerId, bookingId);

        assertEquals(bookingId, resultForBooker.getId());
        assertEquals(resultForOwner, resultForBooker);
    }

    @Test
    void getByUnknownId() {
        long callerId = userHelper.createAndGetId("caller");

        assertBookingNotFound(
                UNKNOWN_ID,
                () -> testee.get(callerId, UNKNOWN_ID)
        );
    }

    @Test
    void getByIdForNeitherOwnerNotBooker() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "item", "item-description", true, null);
        long bookerId = userHelper.createAndGetId("booker");
        long bookingId = bookingHelper.createAndGetId(bookerId, itemId);

        long callerId = userHelper.createAndGetId("caller");
        assertThrows(
                NotFoundException.class,
                () -> testee.get(callerId, bookingId)
        );
    }
}