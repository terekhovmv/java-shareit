package ru.practicum.shareit.gateway.errors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.gateway.errors.dto.ErrorResponseDto;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleValidationException(Throwable throwable) {
        return createErrorResponse(throwable, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleOtherwise(Throwable throwable) {
        log.error("Unexpected error occurred:\n{}", ExceptionUtils.getStackTrace(throwable));
        return createErrorResponse("Unexpected error occurred, contact the support team", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponseDto> createErrorResponse(Throwable throwable, HttpStatus status) {
        return createErrorResponse(throwable.getMessage(), status);
    }

    private ResponseEntity<ErrorResponseDto> createErrorResponse(String error, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponseDto(error), status);
    }
}
