package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

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
    public ItemDto findById(@PathVariable long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<ItemDto> findOwned(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return service.findOwned(requesterId);
    }

    @GetMapping("/search")
    public List<ItemDto> findAvailableByText(@RequestParam("text") String text) {
        return service.findAvailableByText(text);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @Valid @RequestBody ItemDto archetypeDto
    ) {
        return service.create(requesterId, archetypeDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @PathVariable long id,
            @RequestBody ItemDto patchDto
    ) {
        return service.update(requesterId, id, patchDto);
    }
}
