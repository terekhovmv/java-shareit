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
}
