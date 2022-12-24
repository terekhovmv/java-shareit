package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto findById(long id);

    List<ItemDto> findOwned(long ownerId);

    List<ItemDto> findAvailableByText(String text);

    ItemDto create(long ownerId, ItemDto archetypeDto);

    ItemDto update(long requesterId, long id, ItemDto patchDto);
}
