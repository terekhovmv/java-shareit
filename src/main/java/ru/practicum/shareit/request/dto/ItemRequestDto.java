package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemRequestDto {
    Long id;
    String description;
    Long requesterId;
}
