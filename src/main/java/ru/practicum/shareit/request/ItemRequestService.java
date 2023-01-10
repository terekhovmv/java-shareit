package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateDto;

public interface ItemRequestService {
    ItemRequestDto create(long requesterId, ItemRequestUpdateDto dto);
}
