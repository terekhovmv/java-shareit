package ru.practicum.shareit.gateway.booking.dto;

import lombok.Data;
import ru.practicum.shareit.gateway.booking.dto.annotations.ValidBookingPeriod;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ValidBookingPeriod(message = "End date should be later than start date")
public class BookingUpdateDto {
    @NotNull
    Long itemId;
    @FutureOrPresent
    LocalDateTime start;
    @Future
    LocalDateTime end;
}
