package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    List<CommentDto> comments;
    BookingBriefDto lastBooking;
    BookingBriefDto nextBooking;
}
