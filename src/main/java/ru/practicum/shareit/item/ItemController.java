package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    public final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable long id) {
        return service.get(id);
    }

    @GetMapping
    public List<ItemDto> getOwned(@RequestHeader("X-Sharer-User-Id") long callerId) {
        return service.getOwned(callerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableWithText(@RequestParam("text") String text) {
        return service.getAvailableWithText(text);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long callerId,
            @Valid @RequestBody ItemUpdateDto dto
    ) {
        return service.create(callerId, dto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long callerId,
            @PathVariable long id,
            @RequestBody ItemUpdateDto dto
    ) {
        return service.update(callerId, id, dto);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") long callerId,
            @PathVariable long id,
            @Valid @RequestBody CommentUpdateDto dto
    ) {
        return service.addComment(callerId, id, dto);
    }
}
