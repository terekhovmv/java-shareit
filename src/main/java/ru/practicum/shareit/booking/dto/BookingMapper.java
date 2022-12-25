package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

@Component
public class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public BookingMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    public BookingDto toBookingDto(Booking from) {
        BookingDto mapped = new BookingDto();
        mapped.setId(from.getId());
        mapped.setStart(from.getStart());
        mapped.setEnd(from.getEnd());
        mapped.setItem(itemMapper.toItemDto(from.getItem()));
        mapped.setBooker(userMapper.toUserDto(from.getBooker()));
        mapped.setStatus(from.getStatus());
        return mapped;
    }
}
