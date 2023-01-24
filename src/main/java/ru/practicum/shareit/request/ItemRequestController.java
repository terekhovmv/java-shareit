package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItAppConsts;
import ru.practicum.shareit.pagination.RandomAccessPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService service;

    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @Valid @RequestBody ItemRequestUpdateDto dto
    ) {
        return service.create(callerId, dto);
    }

    @GetMapping("/{id}")
    public ItemRequestDto get(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @PathVariable long id
    ) {
        return service.get(callerId, id);
    }

    @GetMapping
    public List<ItemRequestDto> getCreated(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId
    ) {
        return service.getCreated(callerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getFromOtherUsers(
            @RequestHeader(ShareItAppConsts.HEADER_CALLER_ID) long callerId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return service.getFromOtherUsers(
                callerId,
                RandomAccessPageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"))
        );
    }

}
