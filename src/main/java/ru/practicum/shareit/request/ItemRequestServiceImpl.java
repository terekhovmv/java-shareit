package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestUpdateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public ItemRequestDto get(long callerId, long id) {
        userRepository.require(callerId);

        ItemRequest request = itemRequestRepository.require(id);

        return mapper.toDto(
                request,
                itemRepository.getAllByRequestId(id)
        );
    }

    @Override
    public List<ItemRequestDto> getCreated(long creatorId) {
        userRepository.require(creatorId);

        return toDtos(
                itemRequestRepository.getAllByRequesterIdOrderByCreatedDesc(creatorId)
        );
    }

    @Override
    public List<ItemRequestDto> getFromOtherUsers(long callerId, Pageable pageable) {
        userRepository.require(callerId);

        return toDtos(
                itemRequestRepository
                        .getAllByRequesterIdNot(callerId, pageable)
                        .getContent()
        );
    }

    private List<ItemRequestDto> toDtos(List<ItemRequest> requests) {
        List<Long> requestIds = requests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequests =
                itemRepository.getAllByRequestIdIn(requestIds)
                        .stream()
                        .collect(Collectors.groupingBy(Item::getRequestId));

        return requests
                .stream()
                .map(request -> mapper.toDto(
                        request,
                        itemsByRequests.get(request.getId())
                ))
                .collect(Collectors.toList());
    }
}
