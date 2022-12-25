package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    default Booking require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the booking #" + id));
    }

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long bookerId,
            LocalDateTime afterStart,
            LocalDateTime beforeEnd
    );
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime afterEnd);
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime beforeStart);
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);


    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long ownerId,
            LocalDateTime afterStart,
            LocalDateTime beforeEnd
    );
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime afterEnd);
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime beforeStart);
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    Booking getFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime beforeAfter);
    Booking getFirstByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime afterEnd);

    Integer countByItemIdAndBookerIdAndStatusAndEndBefore(
            long itemId,
            long userId,
            BookingStatus status,
            LocalDateTime afterEnd
    );
}
