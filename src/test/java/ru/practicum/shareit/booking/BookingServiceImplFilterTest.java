package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.RandomAccessPageRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplFilterTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    private BookingMapper bookingMapper;

    private BookingServiceImpl testee;

    private User booker;

    private User owner;

    private Booking booking;

    private Pageable defaultPageable;

    @BeforeEach
    void beforeEach() {
        bookingMapper = new BookingMapper(
                new UserMapper(),
                new ItemMapper(new CommentMapper())
        );
        testee = new BookingServiceImpl(
                bookingRepository,
                itemRepository,
                userRepository,
                bookingMapper
        );
        owner = createUser(1);
        booker = createUser(2);
        booking = createBooking(
                1,
                createItem(1, owner),
                booker
        );
        defaultPageable = RandomAccessPageRequest.of(0, 100, Sort.unsorted());
    }

    @Test
    void getCreatedAll() {
        when(
                userRepository.require(booker.getId())
        ).thenReturn(booker);

        when(
                bookingRepository.getAllByBookerId(booker.getId(), defaultPageable)
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getCreated(
                        booker.getId(),
                        BookingFilter.ALL,
                        defaultPageable
                )
        );
    }

    @Test
    void getCreatedWaiting() {
        when(
                userRepository.require(booker.getId())
        ).thenReturn(booker);

        when(
                bookingRepository.getAllByBookerIdAndStatus(
                        booker.getId(),
                        BookingStatus.WAITING,
                        defaultPageable
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getCreated(
                        booker.getId(),
                        BookingFilter.WAITING,
                        defaultPageable
                )
        );
    }

    @Test
    void getCreatedRejected() {
        when(
                userRepository.require(booker.getId())
        ).thenReturn(booker);

        when(
                bookingRepository.getAllByBookerIdAndStatus(
                        booker.getId(),
                        BookingStatus.REJECTED,
                        defaultPageable
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getCreated(
                        booker.getId(),
                        BookingFilter.REJECTED,
                        defaultPageable
                )
        );
    }

    @Test
    void getCreatedPast() {
        when(
                userRepository.require(booker.getId())
        ).thenReturn(booker);

        when(
                bookingRepository.getAllPastForBooker(
                        eq(booker.getId()),
                        any(),
                        eq(defaultPageable)
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getCreated(
                        booker.getId(),
                        BookingFilter.PAST,
                        defaultPageable
                )
        );
    }

    @Test
    void getCreatedFuture() {
        when(
                userRepository.require(booker.getId())
        ).thenReturn(booker);

        when(
                bookingRepository.getAllFutureForBooker(
                        eq(booker.getId()),
                        any(),
                        eq(defaultPageable)
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getCreated(
                        booker.getId(),
                        BookingFilter.FUTURE,
                        defaultPageable
                )
        );
    }

    @Test
    void getCreatedCurrent() {
        when(
                userRepository.require(booker.getId())
        ).thenReturn(booker);

        when(
                bookingRepository.getAllCurrentForBooker(
                        eq(booker.getId()),
                        any(),
                        eq(defaultPageable)
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getCreated(
                        booker.getId(),
                        BookingFilter.CURRENT,
                        defaultPageable
                )
        );
    }

    @Test
    void getForOwnedItemsAll() {
        when(
                userRepository.require(owner.getId())
        ).thenReturn(owner);

        when(
                bookingRepository.getAllByItemOwnerId(owner.getId(), defaultPageable)
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getForOwnedItems(
                        owner.getId(),
                        BookingFilter.ALL,
                        defaultPageable
                )
        );
    }

    @Test
    void getForOwnedItemsWaiting() {
        when(
                userRepository.require(owner.getId())
        ).thenReturn(owner);

        when(
                bookingRepository.getAllByItemOwnerIdAndStatus(
                        owner.getId(),
                        BookingStatus.WAITING,
                        defaultPageable
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getForOwnedItems(
                        owner.getId(),
                        BookingFilter.WAITING,
                        defaultPageable
                )
        );
    }

    @Test
    void getForOwnedItemsRejected() {
        when(
                userRepository.require(owner.getId())
        ).thenReturn(owner);

        when(
                bookingRepository.getAllByItemOwnerIdAndStatus(
                        owner.getId(),
                        BookingStatus.REJECTED,
                        defaultPageable
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getForOwnedItems(
                        owner.getId(),
                        BookingFilter.REJECTED,
                        defaultPageable
                )
        );
    }

    @Test
    void getForOwnedItemsPast() {
        when(
                userRepository.require(owner.getId())
        ).thenReturn(owner);

        when(
                bookingRepository.getAllPastForItemOwner(
                        eq(owner.getId()),
                        any(),
                        eq(defaultPageable)
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getForOwnedItems(
                        owner.getId(),
                        BookingFilter.PAST,
                        defaultPageable
                )
        );
    }

    @Test
    void getForOwnedItemsFuture() {
        when(
                userRepository.require(owner.getId())
        ).thenReturn(owner);

        when(
                bookingRepository.getAllFutureForItemOwner(
                        eq(owner.getId()),
                        any(),
                        eq(defaultPageable)
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getForOwnedItems(
                        owner.getId(),
                        BookingFilter.FUTURE,
                        defaultPageable
                )
        );
    }

    @Test
    void getForOwnedItemsCurrent() {
        when(
                userRepository.require(owner.getId())
        ).thenReturn(owner);

        when(
                bookingRepository.getAllCurrentForItemOwner(
                        eq(owner.getId()),
                        any(),
                        eq(defaultPageable)
                )
        ).thenReturn(new PageImpl<>(List.of(booking)));

        assertIterableEquals(
                Stream.of(booking)
                        .map(bookingMapper::toDto)
                        .collect(Collectors.toList()),
                testee.getForOwnedItems(
                        owner.getId(),
                        BookingFilter.CURRENT,
                        defaultPageable
                )
        );
    }

    private User createUser(long id) {
        User result = new User();
        result.setId(id);
        result.setName("user-" + id);
        result.setEmail("user" + id + "@abc.def");
        return result;
    }

    private Item createItem(long id, User owner) {
        Item result = new Item();
        result.setId(id);
        result.setName("item-" + id);
        result.setDescription("item-description-" + id);
        result.setAvailable(true);
        result.setOwner(owner);
        return result;
    }

    private Booking createBooking(long id, Item item, User booker) {
        Booking result = new Booking();
        result.setId(id);
        result.setItem(item);
        result.setBooker(booker);
        return result;
    }

}
