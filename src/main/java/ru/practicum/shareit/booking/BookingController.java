package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItAppConsts;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.pagination.RandomAccessPageRequest;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto create(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @Valid @RequestBody BookingUpdateDto dto
    ) {
        return service.create(callerId, dto);
    }

    @PatchMapping("/{id}")
    public BookingDto setApproved(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id,
            @RequestParam("approved") boolean value
    ) {
        return service.setApproved(callerId, id, value);
    }

    @GetMapping("/{id}")
    public BookingDto get(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id
    ) {
        return service.get(callerId, id);
    }

    @GetMapping
    public List<BookingDto> getCreated(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return service.getCreated(
                callerId,
                parseBookingFilter(state),
                RandomAccessPageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"))
        );
    }

    @GetMapping("/owner")
    public List<BookingDto> getForOwnedItems(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return service.getForOwnedItems(
                callerId,
                parseBookingFilter(state),
                RandomAccessPageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"))
        );
    }

    private BookingFilter parseBookingFilter(String value) {
        try {
            return BookingFilter.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Unknown state: " + value);
        }
    }
}
