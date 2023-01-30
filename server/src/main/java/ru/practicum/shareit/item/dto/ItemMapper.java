package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
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
        return toDto(from, null, null, null);
    }

    public ItemDto toDto(Item from, List<Comment> comments) {
        return toDto(from, comments, null, null);
    }

    public ItemDto toDto(
            Item from,
            List<Comment> comments,
            Booking lastBooking,
            Booking nextBooking
    ) {
        ItemDto mapped = new ItemDto();

        mapped.setId(from.getId());
        mapped.setName(from.getName());
        mapped.setDescription(from.getDescription());
        mapped.setRequestId(from.getRequestId());
        mapped.setAvailable(from.getAvailable());

        mapped.setComments(
                comments != null
                        ? comments.stream().map(commentMapper::toDto).collect(Collectors.toList())
                        : List.of()
        );

        if (lastBooking != null) {
            mapped.setLastBooking(
                    bookingToBookingBriefDto(lastBooking)
            );
        }
        if (nextBooking != null) {
            mapped.setNextBooking(
                    bookingToBookingBriefDto(nextBooking)
            );
        }

        return mapped;
    }

    private BookingBriefDto bookingToBookingBriefDto(Booking from) {
        BookingBriefDto mapped = new BookingBriefDto();

        mapped.setId(from.getId());
        mapped.setBookerId(from.getBooker().getId());

        return mapped;
    }
}
