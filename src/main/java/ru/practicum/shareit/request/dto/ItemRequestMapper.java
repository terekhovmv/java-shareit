package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public final class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest from) {
        return ItemRequestDto.builder()
                .id(from.getId())
                .description(from.getDescription())
                .requesterId(from.getRequesterId())
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto from) {
        return ItemRequest.builder()
                .id(from.getId())
                .description(from.getDescription())
                .requesterId(from.getRequesterId())
                .build();
    }
}
