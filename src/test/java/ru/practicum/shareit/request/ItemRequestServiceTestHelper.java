package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateDto;

public class ItemRequestServiceTestHelper {
    private final ItemRequestService service;

    public ItemRequestServiceTestHelper(ItemRequestService service) {
        this.service = service;
    }

    public static ItemRequestUpdateDto makeItemRequestUpdateDto(String description) {
        ItemRequestUpdateDto result = new ItemRequestUpdateDto();
        result.setDescription(description);
        return result;
    }

    public ItemRequestDto create(long requesterId, String description) {
        return service.create(requesterId, makeItemRequestUpdateDto(description));
    }

    public long createAndGetId(long requesterId, String description) {
        return create(requesterId, description).getId();
    }
}
