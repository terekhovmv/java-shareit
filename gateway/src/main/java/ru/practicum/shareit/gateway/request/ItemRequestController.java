package ru.practicum.shareit.gateway.request;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.ShareItAppConsts;
import ru.practicum.shareit.gateway.request.dto.ItemRequestUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    public ItemRequestController(ItemRequestClient client) {
        this.client = client;
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @Valid @RequestBody ItemRequestUpdateDto dto
    ) {
        return client.create(callerId, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id
    ) {
        return client.get(callerId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getCreated(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId
    ) {
        return client.getCreated(callerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getFromOtherUsers(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "20") @Positive int size
    ) {
        return client.getFromOtherUsers(callerId, from, size);
    }
}
