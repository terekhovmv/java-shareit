package ru.practicum.shareit.booking.dto.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import java.time.LocalDateTime;

public class BookingPeriodValidator implements ConstraintValidator<ValidBookingPeriod, BookingUpdateDto> {

    @Override
    public boolean isValid(BookingUpdateDto dto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();

        if (start == null || end == null) {
            return true;
        }
        return end.isAfter(start);
    }
}
