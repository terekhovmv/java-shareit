package ru.practicum.shareit.errors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.exceptions.AlreadyApprovedBookingException;
import ru.practicum.shareit.booking.exceptions.UnableToCreateBookingException;
import ru.practicum.shareit.booking.exceptions.UnableToManageBookingException;
import ru.practicum.shareit.booking.exceptions.UnavailableForBookingException;
import ru.practicum.shareit.errors.dto.ErrorResponseDto;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.exceptions.NotRealBookerException;
import ru.practicum.shareit.user.UserController;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        UserController.class,
        ItemController.class,
        BookingController.class
})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleForbiddenException(ForbiddenAccessException exception) {
        return createErrorResponse(exception);
    }

    @ExceptionHandler({
            NotFoundException.class,

            UnableToCreateBookingException.class, // issue in Postman tests, this should be FORBIDDEN
            UnableToManageBookingException.class  // issue in Postman tests, this should be FORBIDDEN
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFoundException(Throwable throwable) {
        return createErrorResponse(throwable);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleConflictException(Throwable throwable) {
        return createErrorResponse(throwable);
    }

    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class,

            AlreadyApprovedBookingException.class,
            UnavailableForBookingException.class,
            NotRealBookerException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationException(Throwable throwable) {
        return createErrorResponse(throwable);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleOtherwise(Throwable throwable) {
        log.error("Unexpected error occurred:\n{}", ExceptionUtils.getStackTrace(throwable));
        return new ErrorResponseDto("Unexpected error occurred, contact the support team");
    }

    private ErrorResponseDto createErrorResponse(Throwable throwable) {
        return new ErrorResponseDto(throwable.getMessage());
    }
}
