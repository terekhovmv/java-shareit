package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    boolean contains(long id);

    default void requireContains(long id) {
        if (!contains(id)) {
            throw new NotFoundException("Unable to find the item #" + id);
        }
    }

    Item findById(long id);

    List<Item> findByOwnerId(long ownerId);

    List<Item> findAvailableByText(String text);

    Item create(Item archetype);

    Item update(long id, Item patch);
}
