package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItAppConsts;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.pagination.RandomAccessPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    public final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ItemDto get(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id
    ) {
        return service.get(callerId, id);
    }

    @GetMapping
    public List<ItemDto> getOwned(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return service.getOwned(
                callerId,
                RandomAccessPageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"))
        );
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableWithText(
            @RequestParam("text") String text,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return service.getAvailableWithText(
                text,
                RandomAccessPageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"))
        );
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @Valid @RequestBody ItemUpdateDto dto
    ) {
        return service.create(callerId, dto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id,
            @RequestBody ItemUpdateDto dto
    ) {
        return service.update(callerId, id, dto);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id,
            @Valid @RequestBody CommentUpdateDto dto
    ) {
        return service.addComment(callerId, id, dto);
    }
}
