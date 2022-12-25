package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(
            UserRepository userRepository,
            ItemRepository itemRepository,
            ItemMapper itemMapper
    ) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto findById(long id) {
        return itemMapper.toItemDto(
                itemRepository.require(id)
        );
    }

    @Override
    public List<ItemDto> findOwned(long ownerId) {
        return itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findAvailableByText(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.findAllAvailableByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(long ownerId, ItemDto archetypeDto) {
        User owner = userRepository.require(ownerId);

        Item archetype = itemMapper.toItem(archetypeDto);
        archetype.setId(null);
        archetype.setOwner(owner);

        Item created = itemRepository.save(archetype);
        log.info(
                "Item {} owned by user #{} was successfully added with id {}",
                created.getName(),
                ownerId,
                created.getId()
        );
        return itemMapper.toItemDto(created);
    }

    @Override
    public ItemDto update(long callerId, long id, ItemDto patchDto) {
        userRepository.require(callerId);
        Item toUpdate = itemRepository.require(id);
        if (!Objects.equals(callerId, toUpdate.getOwner().getId())) {
            throw new ForbiddenAccessException("Unauthorized access to item #" + id);
        }

        itemMapper.patch(patchDto, toUpdate);

        Item updated = itemRepository.save(toUpdate);
        log.info("Item #{} was successfully updated by user #{}", updated.getId(), callerId);
        return itemMapper.toItemDto(updated);
    }
}
