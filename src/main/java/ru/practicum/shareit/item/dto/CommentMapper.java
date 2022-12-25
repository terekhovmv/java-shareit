package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment from) {
        CommentDto mapped = new CommentDto();
        mapped.setId(from.getId());
        mapped.setText(from.getText());
        mapped.setAuthorName(
                (from.getAuthor() != null)
                        ? from.getAuthor().getName()
                        : ""
        );
        mapped.setCreated(from.getCreated());
        return mapped;
    }
}
