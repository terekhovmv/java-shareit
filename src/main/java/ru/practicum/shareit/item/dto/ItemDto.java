package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    List<CommentDto> comments;
}
