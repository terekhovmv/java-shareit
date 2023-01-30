package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto get(long callerId, long id);

    List<ItemDto> getOwned(long ownerId, Pageable pageable);

    List<ItemDto> getAvailableWithText(String text, Pageable pageable);

    ItemDto create(long ownerId, ItemUpdateDto dto);

    ItemDto update(long callerId, long id, ItemUpdateDto dto);

    CommentDto addComment(long authorId, long id, CommentUpdateDto dto);
}
