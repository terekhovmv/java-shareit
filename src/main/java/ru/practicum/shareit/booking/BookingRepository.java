package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    default Booking require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the booking #" + id));
    }

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> getAllForBooker(long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> getInStatusForBooker(long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start <= ?2 AND b.end >= ?2 ORDER BY b.start DESC")
    List<Booking> getCurrentForBooker(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> getPastForBooker(long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> getFutureForBooker(long bookerId, LocalDateTime now);


    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    List<Booking> getAllForOwner (long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> getInStatusForOwner (long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start <= ?2 AND b.end >= ?2 ORDER BY b.start DESC")
    List<Booking> getCurrentForOwner (long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> getPastForOwner (long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> getFutureForOwner (long bookerId, LocalDateTime now);


    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.end < ?2 ORDER BY b.end DESC")
    Booking getLastForItem(long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.start > ?2 ORDER BY b.start ASC")
    Booking getNextForItem(long itemId, LocalDateTime now);


    @Query(
            "SELECT COUNT (b) FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
                "AND b.item.id = ?2 " +
                "AND b.end < ?3 " +
                "AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED"
    )
    Integer getFinishedCount(long bookerId, long itemId, LocalDateTime now);
}
