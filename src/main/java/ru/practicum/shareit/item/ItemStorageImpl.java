package ru.practicum.shareit.item;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.events.OnDeleteUserEvent;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> storage = new HashMap<>();
    private long lastId = 1;

    @Override
    public boolean contains(long id) {
        return storage.containsKey(id);
    }

    @Override
    public Item findById(long id) {
        requireContains(id);
        return storage.get(id);
    }

    @Override
    public List<Item> findByOwnerId(long ownerId) {
        return storage.values()
                .stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAvailableByText(String text) {
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }

        String lowerText = text.toLowerCase();
        return storage.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerText)
                        || item.getDescription().toLowerCase().contains(lowerText)
                )
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item archetype) {
        final long id = lastId++;
        Item created = archetype.toBuilder()
                .id(id)
                .build();

        storage.put(id, created);
        return created;
    }

    @Override
    public Item update(long id, Item patch) {
        Item.ItemBuilder builder = findById(id).toBuilder();

        if (StringUtils.isNotBlank(patch.getName())) {
            builder.name(patch.getName());
        }

        if (StringUtils.isNotBlank(patch.getDescription())) {
            builder.description(patch.getDescription());
        }

        if (patch.getAvailable() != null) {
            builder.available(patch.getAvailable());
        }

        if (patch.getOwnerId() != null) {
            builder.ownerId(patch.getOwnerId());
        }

        Item updated = builder.build();
        storage.put(id, updated);
        return updated;
    }

    @EventListener
    public void onDeleteUser(OnDeleteUserEvent event) {
        Iterator<Long> it = storage.keySet().iterator();
        while (it.hasNext()) {
            Item item = storage.get(it.next());
            if (item.getOwnerId() == event.getId()) {
                it.remove();
            }
        }
    }
}