package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.annotations.ValidBookingPeriod;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@ValidBookingPeriod(message = "End date should be later than start date")
public class BookingRequestDto {
    Long itemId;
    @FutureOrPresent
    LocalDateTime start;
    @FutureOrPresent
    LocalDateTime end;
}
