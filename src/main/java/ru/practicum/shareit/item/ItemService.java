package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item findById(long id);

    List<Item> findOwned(long ownerId);

    List<Item> findAvailableByText(String text);

    Item create(long ownerId, Item archetype);

    Item update(long requesterId, long id, Item patch);
}
