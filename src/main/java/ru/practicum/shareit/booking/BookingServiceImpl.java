package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
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
    public BookingDto create(long callerId, BookingUpdateDto dto) {
        User caller = userRepository.require(callerId);
        Item item = itemRepository.require(dto.getItemId());

        if (Objects.equals(callerId, item.getOwner().getId())) {
            throw new UnableToCreateBookingException("Unable to book owned item #" + item.getId());
        }

        if (!item.getAvailable()) {
            throw new UnavailableForBookingException("Item #" + item.getId() + " is not available");
        }

        Booking archetype = new Booking();
        archetype.setStart(dto.getStart());
        archetype.setEnd(dto.getEnd());
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
        return bookingMapper.toDto(created);
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
        return bookingMapper.toDto(updated);
    }

    @Override
    public BookingDto get(long callerId, long id) {
        userRepository.require(callerId);
        Booking booking = bookingRepository.require(id);

        if (
                !Objects.equals(callerId, booking.getBooker().getId()) &&
                !Objects.equals(callerId, booking.getItem().getOwner().getId())
        ) {
            throw new NotFoundException("Unable to access booking #" + id);
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getCreated(long bookerId, BookingFilter filter) {
        userRepository.require(bookerId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> found = null;
        switch (filter) {
            case ALL:
                found = bookingRepository.getAllForBooker(bookerId);
                break;
            case WAITING:
                found = bookingRepository.getInStatusForBooker(bookerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                found = bookingRepository.getInStatusForBooker(bookerId, BookingStatus.REJECTED);
                break;
            case PAST:
                found = bookingRepository.getPastForBooker(bookerId, now);
                break;
            case FUTURE:
                found = bookingRepository.getFutureForBooker(bookerId, now);
                break;
            case CURRENT:
                found = bookingRepository.getCurrentForBooker(bookerId, now);
                break;
        }

        return found
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getForOwnedItems(long ownerId, BookingFilter filter) {
        userRepository.require(ownerId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> found = null;
        switch (filter) {
            case ALL:
                found = bookingRepository.getAllForOwner(ownerId);
                break;
            case WAITING:
                found = bookingRepository.getInStatusForOwner(ownerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                found = bookingRepository.getInStatusForOwner(ownerId, BookingStatus.REJECTED);
                break;
            case PAST:
                found = bookingRepository.getPastForOwner(ownerId, now);
                break;
            case FUTURE:
                found = bookingRepository.getFutureForOwner(ownerId, now);
                break;
            case CURRENT:
                found = bookingRepository.getCurrentForOwner(ownerId, now);
                break;
        }

        return found
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
