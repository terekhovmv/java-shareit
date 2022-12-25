package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    private final CommentMapper commentMapper;

    public ItemMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public ItemDto toDto(Item from) {
        ItemDto mapped = new ItemDto();
        mapped.setId(from.getId());
        mapped.setName(from.getName());
        mapped.setDescription(from.getDescription());
        mapped.setAvailable(from.getAvailable());
        return mapped;
    }

    public ItemDto toDto(Item from, List<Comment> comments) {
        ItemDto mapped = toDto(from);
        mapped.setComments(
                comments
                        .stream()
                        .map(commentMapper::toDto)
                        .collect(Collectors.toList())

        );
        return mapped;
    }
}
