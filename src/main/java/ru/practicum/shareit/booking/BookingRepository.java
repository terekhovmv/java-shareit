package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    default Booking require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the booking #" + id));
    }

    Page<Booking> getAllByBookerId(long bookerId, Pageable pageable);

    Page<Booking> getAllByBookerIdAndStatus(long bookerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start <= ?2 AND b.end >= ?2 ORDER BY b.start DESC")
    Page<Booking> getAllCurrentForBooker(long bookerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    Page<Booking> getAllPastForBooker(long bookerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    Page<Booking> getAllFutureForBooker(long bookerId, LocalDateTime now, Pageable pageable);


    Page<Booking> getAllByItemOwnerId(long ownerId, Pageable pageable);

    Page<Booking> getAllByItemOwnerIdAndStatus(long ownerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start <= ?2 AND b.end >= ?2 ORDER BY b.start DESC")
    Page<Booking> getAllCurrentForItemOwner(long bookerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    Page<Booking> getAllPastForItemOwner(long bookerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    Page<Booking> getAllFutureForItemOwner(long bookerId, LocalDateTime now, Pageable pageable);


    Booking getFirstByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime now);

    Booking getFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime now);

    List<Booking> getAllByItemIdIn(Collection<Long> itemIds);

    @Query(
            "SELECT COUNT (b) FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
                "AND b.item.id = ?2 " +
                "AND b.end < ?3 " +
                "AND b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED"
    )
    Integer getFinishedCount(long bookerId, long itemId, LocalDateTime now);
}
