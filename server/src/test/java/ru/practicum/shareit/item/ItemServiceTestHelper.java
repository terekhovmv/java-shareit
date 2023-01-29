package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

public class ItemServiceTestHelper {
    private final ItemService service;

    public ItemServiceTestHelper(ItemService service) {
        this.service = service;
    }

    public static ItemUpdateDto makeItemUpdateDto(String name, String description, Boolean available, Long requesterId) {
        ItemUpdateDto result = new ItemUpdateDto();
        result.setName(name);
        result.setDescription(description);
        result.setAvailable(available);
        result.setRequestId(requesterId);
        return result;
    }

    public static ItemUpdateDto makeItemUpdateDto(String name, Long requesterId) {
        return makeItemUpdateDto(name, name + "-description", true, requesterId);
    }

    public static CommentUpdateDto makeCommentUpdateDto(String text) {
        CommentUpdateDto result = new CommentUpdateDto();
        result.setText(text);
        return result;
    }

    public ItemDto create(long ownerId, String name, String description, Boolean available, Long requesterId) {
        return service.create(ownerId, makeItemUpdateDto(name, description, available, requesterId));
    }

    public ItemDto create(long ownerId, String name, Long requesterId) {
        return service.create(ownerId, makeItemUpdateDto(name, requesterId));
    }

    public ItemDto create(long ownerId, String name) {
        return create(ownerId, name, null);
    }

    public long createAndGetId(long ownerId, String name, String description, Boolean available, Long requesterId) {
        return create(ownerId, name, description, available, requesterId).getId();
    }

    public long createAndGetId(long ownerId, String name, Long requesterId) {
        return create(ownerId, name, requesterId).getId();
    }

    public long createAndGetId(long ownerId, String name) {
        return createAndGetId(ownerId, name, null);
    }
}
