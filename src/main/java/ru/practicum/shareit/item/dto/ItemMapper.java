package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDto toDto(Item from) {
        ItemDto mapped = new ItemDto();
        mapped.setId(from.getId());
        mapped.setName(from.getName());
        mapped.setDescription(from.getDescription());
        mapped.setAvailable(from.getAvailable());
        return mapped;
    }
}
