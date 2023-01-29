package ru.practicum.shareit.gateway.booking.dto.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = BookingPeriodValidator.class)
public @interface ValidBookingPeriod {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}