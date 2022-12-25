package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    default Booking require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the booking #" + id));
    }
}
