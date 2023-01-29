package ru.practicum.shareit.gateway.item;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.ShareItAppConsts;
import ru.practicum.shareit.gateway.item.dto.CommentUpdateDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    public final ItemClient client;

    public ItemController(ItemClient client) {
        this.client = client;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id
    ) {
        return client.get(callerId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getOwned(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "20") @Positive int size
    ) {
        return client.getOwned(callerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailableWithText(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam("text") String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "20") @Positive int size
    ) {
        return client.getAvailableWithText(callerId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @Valid @RequestBody ItemUpdateDto dto
    ) {
        return client.create(callerId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id,
            @RequestBody ItemUpdateDto dto
    ) {
        return client.update(callerId, id, dto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id,
            @Valid @RequestBody CommentUpdateDto dto
    ) {
        return client.addComment(callerId, id, dto);
    }
}
