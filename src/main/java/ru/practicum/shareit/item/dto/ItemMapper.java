package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item from) {
        return ItemDto.builder()
                .id(from.getId())
                .name(from.getName())
                .description(from.getDescription())
                .available(from.getAvailable())
                .build();
    }

    public Item toItem(ItemDto from) {
        return Item.builder()
                .id(from.getId())
                .name(from.getName())
                .description(from.getDescription())
                .available(from.getAvailable())
                .build();
    }
}
