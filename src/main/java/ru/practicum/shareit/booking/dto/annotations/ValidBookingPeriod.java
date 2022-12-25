package ru.practicum.shareit.booking.dto.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = BookingPeriodValidator.class)
public @interface ValidBookingPeriod {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}