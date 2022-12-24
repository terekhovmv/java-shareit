package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userStorage;
    private final ItemStorage itemStorage;

    public ItemServiceImpl(UserRepository userStorage, ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public Item findById(long id) {
        return itemStorage.findById(id);
    }

    @Override
    public List<Item> findOwned(long ownerId) {
        return itemStorage.findByOwnerId(ownerId);
    }

    @Override
    public List<Item> findAvailableByText(String text) {
        return itemStorage.findAvailableByText(text);
    }

    @Override
    public Item create(long ownerId, Item archetype) {
        userStorage.require(ownerId);

        Item created = itemStorage.create(
                archetype.toBuilder()
                        .ownerId(ownerId)
                        .build()
        );
        log.info(
                "Item {} owned by user #{} was successfully added with id {}",
                created.getName(),
                ownerId,
                created.getId()
        );
        return created;
    }

    @Override
    public Item update(long requesterId, long id, Item patch) {
        requireAuthorizedAccess(requesterId, id);

        Item updated = itemStorage.update(id, patch);
        log.info("Item #{} was successfully updated by user #{}", updated.getId(), requesterId);
        return updated;
    }

    private void requireAuthorizedAccess(long requesterId, long id) {
        userStorage.require(requesterId);

        Item known = itemStorage.findById(id);
        if (!Objects.equals(requesterId, known.getOwnerId())) {
            throw new ForbiddenAccessException("Unauthorized to update item #" + id);
        }
    }
}
