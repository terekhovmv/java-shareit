package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Item findById(long id) {
        return itemRepository.require(id);
    }

    @Override
    public List<Item> findOwned(long ownerId) {
        return itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId);
    }

    @Override
    public List<Item> findAvailableByText(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(itemRepository.findAllAvailableByText(text));

    }

    @Override
    public Item create(long ownerId, Item archetype) {
        User owner = userRepository.require(ownerId);
        archetype.setOwner(owner);

        Item created = itemRepository.save(archetype);
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
        Item toUpdate = requireAuthorized(requesterId, id);
        patch(patch, toUpdate);

        Item updated = itemRepository.save(toUpdate);
        log.info("Item #{} was successfully updated by user #{}", updated.getId(), requesterId);
        return updated;
    }

    private Item requireAuthorized(long requesterId, long id) {
        userRepository.require(requesterId);

        Item known = itemRepository.require(id);
        if (!Objects.equals(requesterId, known.getOwner().getId())) {
            throw new ForbiddenAccessException("Unauthorized to update item #" + id);
        }
        return known;
    }

    private void patch(Item patch, Item destination) {
        if (StringUtils.isNotBlank(patch.getName())) {
            destination.setName(patch.getName());
        }

        if (StringUtils.isNotBlank(patch.getDescription())) {
            destination.setDescription(patch.getDescription());
        }

        if (patch.getAvailable() != null) {
            destination.setAvailable(patch.getAvailable());
        }

        if (patch.getOwner() != null) {
            destination.setOwner(patch.getOwner());
        }

        if (patch.getRequestId() != null) {
            destination.setRequestId(patch.getRequestId());
        }
    }
}
