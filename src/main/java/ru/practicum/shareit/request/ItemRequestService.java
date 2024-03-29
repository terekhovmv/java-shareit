package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long requesterId, ItemRequestUpdateDto dto);

    ItemRequestDto get(long callerId, long id);

    List<ItemRequestDto> getCreated(long creatorId);

    List<ItemRequestDto> getFromOtherUsers(long callerId, Pageable pageable);
}
