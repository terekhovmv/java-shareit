package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestUpdateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final ItemRequestMapper mapper;

    public ItemRequestServiceImpl(
            ItemRequestRepository itemRequestRepository,
            UserRepository userRepository,
            ItemRepository itemRepository,
            ItemRequestMapper mapper
    ) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    public ItemRequestDto create(long requesterId, ItemRequestUpdateDto dto) {
        User requester = userRepository.require(requesterId);

        ItemRequest archetype = new ItemRequest();
        archetype.setDescription(dto.getDescription());
        archetype.setRequester(requester);
        archetype.setCreated(LocalDateTime.now());

        ItemRequest created = itemRequestRepository.save(archetype);
        log.info(
                "Item request '{}' from user #{} was successfully added with id {}",
                created.getDescription(),
                requesterId,
                created.getId()
        );
        return mapper.toDto(created);
    }
}
