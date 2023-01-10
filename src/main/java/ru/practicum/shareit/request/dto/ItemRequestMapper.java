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
        ItemRequestDto mapped = new ItemRequestDto();
        mapped.setId(from.getId());
        mapped.setDescription(from.getDescription());
        mapped.setCreated(from.getCreated());
        return mapped;
    }

    public ItemRequestDto toDto(ItemRequest from, List<Item> items) {
        ItemRequestDto mapped = toDto(from);

        if (items != null) {
            mapped.setItems(
                    items
                            .stream()
                            .map(itemMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        return mapped;
    }
}
