package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    public final ItemService service;
    private final ItemMapper mapper;

    public ItemController(ItemService service, ItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable long id) {
        return mapper.toItemDto(
                service.findById(id)
        );
    }

    @GetMapping
    public List<ItemDto> findOwned(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return service.findOwned(requesterId)
                .stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> findAvailableByText(@RequestParam("text") String text) {
        return service.findAvailableByText(text)
                .stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @Valid @RequestBody ItemDto archetypeDto
    ) {
        return mapper.toItemDto(
                service.create(requesterId, mapper.toItem(archetypeDto))
        );
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long requesterId,
            @PathVariable long id,
            @RequestBody ItemDto patchDto
    ) {
        return mapper.toItemDto(
                service.update(requesterId, id, mapper.toItem(patchDto))
        );
    }
}
