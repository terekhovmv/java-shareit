package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exceptions.UnavailableException;
import ru.practicum.shareit.item.exceptions.UnavailableForOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

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

        if (!item.getAvailable()) {
            throw new UnavailableException("Item #" + item.getId() + " is not available");
        }
        if (Objects.equals(callerId, item.getOwner().getId())) {
            throw new UnavailableForOwnerException("Unable to book item #" + item.getId() + " by item owner");
        }

        Booking archetype = new Booking();
        archetype.setStart(bookingRequestDto.getStart());
        archetype.setEnd(bookingRequestDto.getEnd());
        archetype.setItem(item);
        archetype.setBooker(caller);
        archetype.setStatus(BookingStatus.WAITING);

        Booking created = bookingRepository.save(archetype);
        log.info("The booking of item #{} (owned by user #{}) was successfully created for user #{} ",
                item.getId(),
                item.getOwner().getId(),
                callerId
        );
        return bookingMapper.toBookingDto(created);
    }
}
