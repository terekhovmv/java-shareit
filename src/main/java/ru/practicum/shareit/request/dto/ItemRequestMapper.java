package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
public final class ItemRequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequestMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public ItemRequestDto toDto(ItemRequest from) {
        return toDto(from, null);
    }

    public ItemRequestDto toDto(ItemRequest from, List<Item> items) {
        ItemRequestDto mapped = new ItemRequestDto();

        mapped.setId(from.getId());
        mapped.setDescription(from.getDescription());
        mapped.setCreated(from.getCreated());

        mapped.setItems(
                items != null
                ? items
                        .stream()
                        .map(itemMapper::toDto)
                        .collect(Collectors.toList())
                : List.of()
        );

        return mapped;
    }
}
