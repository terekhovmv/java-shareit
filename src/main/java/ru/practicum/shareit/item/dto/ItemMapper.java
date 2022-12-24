package ru.practicum.shareit.item.dto;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item from) {
        ItemDto mapped = new ItemDto();
        mapped.setId(from.getId());
        mapped.setName(from.getName());
        mapped.setDescription(from.getDescription());
        mapped.setAvailable(from.getAvailable());
        return mapped;
    }

    public Item toItem(ItemDto from) {
        Item mapped = new Item();
        mapped.setId(from.getId());
        mapped.setName(from.getName());
        mapped.setDescription(from.getDescription());
        mapped.setAvailable(from.getAvailable());
        return mapped;
    }

    public void patch(ItemDto patch, Item applyTo) {
        if (StringUtils.isNotBlank(patch.getName())) {
            applyTo.setName(patch.getName());
        }

        if (StringUtils.isNotBlank(patch.getDescription())) {
            applyTo.setDescription(patch.getDescription());
        }

        if (patch.getAvailable() != null) {
            applyTo.setAvailable(patch.getAvailable());
        }
    }
}
