package ru.practicum.shareit.errors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.validation.ValidationException;

import ru.practicum.shareit.errors.dto.ErrorResponseDto;
import ru.practicum.shareit.exceptions.DataConflictException;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        UserController.class,
        ItemController.class
})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFoundException(NotFoundException exception) {
        return createErrorResponse(exception);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleDataConflictException(DataConflictException exception) {
        return createErrorResponse(exception);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleDataConflictException(ForbiddenAccessException exception) {
        return createErrorResponse(exception);
    }

    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class
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
