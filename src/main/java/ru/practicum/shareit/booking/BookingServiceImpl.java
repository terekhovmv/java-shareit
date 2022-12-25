package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exceptions.AlreadyApprovedBookingException;
import ru.practicum.shareit.booking.exceptions.UnableToManageBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.booking.exceptions.UnavailableForBookingException;
import ru.practicum.shareit.booking.exceptions.UnableToCreateBookingException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            ItemRepository itemRepository,
            UserRepository userRepository,
            BookingMapper bookingMapper
    ) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDto book(long callerId, BookingRequestDto bookingRequestDto) {
        User caller = userRepository.require(callerId);
        Item item = itemRepository.require(bookingRequestDto.getItemId());

        if (Objects.equals(callerId, item.getOwner().getId())) {
            throw new UnableToCreateBookingException("Unable to book owned item #" + item.getId());
        }

        if (!item.getAvailable()) {
            throw new UnavailableForBookingException("Item #" + item.getId() + " is not available");
        }

        Booking archetype = new Booking();
        archetype.setStart(bookingRequestDto.getStart());
        archetype.setEnd(bookingRequestDto.getEnd());
        archetype.setItem(item);
        archetype.setBooker(caller);
        archetype.setStatus(BookingStatus.WAITING);

        Booking created = bookingRepository.save(archetype);
        log.info(
                "The booking of item #{} (owned by user #{}) was successfully created for user #{}",
                item.getId(),
                item.getOwner().getId(),
                callerId
        );
        return bookingMapper.toBookingDto(created);
    }

    @Override
    public BookingDto setApproved(long callerId, long id, boolean value) {
        userRepository.require(callerId);
        Booking booking = bookingRepository.require(id);

        if (!Objects.equals(callerId, booking.getItem().getOwner().getId())) {
            throw new UnableToManageBookingException("Unable to approve/reject booking #" + id);
        }

        BookingStatus prevStatus = booking.getStatus();
        if (prevStatus == BookingStatus.APPROVED) {
            throw new AlreadyApprovedBookingException("Booking #" + id + " is already approved");
        }

        BookingStatus newStatus = value ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        Booking updated = bookingRepository.save(booking);
        log.debug(
                "The status booking of #{} was changed from {} to {}",
                id,
                prevStatus,
                newStatus
        );
        return bookingMapper.toBookingDto(updated);
    }

    @Override
    public BookingDto findById(long callerId, long id) {
        userRepository.require(callerId);
        Booking booking = bookingRepository.require(id);

        if (
                !Objects.equals(callerId, booking.getBooker().getId()) &&
                !Objects.equals(callerId, booking.getItem().getOwner().getId())
        ) {
            throw new NotFoundException("Unable to access booking #" + id);
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getCreated(long bookerId, BookingFilter filter) {
        userRepository.require(bookerId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> found = null;
        switch (filter) {
            case ALL:
                found = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
            case WAITING:
                found = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId,
                        BookingStatus.WAITING
                );
                break;
            case REJECTED:
                found = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId,
                        BookingStatus.REJECTED
                );
                break;
            case PAST:
                found = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        bookerId,
                        now
                );
                break;
            case FUTURE:
                found = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId,
                        now
                );
                break;
            case CURRENT:
                found = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId,
                        now,
                        now
                );
                break;
        }

        return found
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getForOwnedItems(long ownerId, BookingFilter filter) {
        userRepository.require(ownerId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> found = null;
        switch (filter) {
            case ALL:
                found = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case WAITING:
                found = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId,
                        BookingStatus.WAITING
                );
                break;
            case REJECTED:
                found = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId,
                        BookingStatus.REJECTED
                );
                break;
            case PAST:
                found = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        ownerId,
                        now
                );
                break;
            case FUTURE:
                found = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        ownerId,
                        now
                );
                break;
            case CURRENT:
                found = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId,
                        now,
                        now
                );
                break;
        }

        return found
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
