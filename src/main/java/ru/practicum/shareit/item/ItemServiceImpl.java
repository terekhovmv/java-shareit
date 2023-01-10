package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exceptions.NotRealBookerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    public ItemServiceImpl(
            UserRepository userRepository,
            ItemRepository itemRepository,
            CommentRepository commentRepository,
            BookingRepository bookingRepository,
            ItemMapper itemMapper,
            CommentMapper commentMapper
    ) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public ItemDto get(long callerId, long id) {
        Item item = itemRepository.require(id);

        if (Objects.equals(callerId, item.getOwner().getId())) {
            LocalDateTime now = LocalDateTime.now();
            return itemMapper.toDto(
                    item,
                    commentRepository.getAllByItemIdOrderByCreatedDesc(id),
                    bookingRepository.getLastForItem(id, now),
                    bookingRepository.getNextForItem(id, now)
            );
        }

        return itemMapper.toDto(
                item,
                commentRepository.getAllByItemIdOrderByCreatedDesc(id)
        );
    }

    @Override
    public List<ItemDto> getOwned(long ownerId) {
        List<Item> items = itemRepository.getAllOwned(ownerId);
        List<Long> itemIds = items
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItems =
                commentRepository.getAllByItemIdInOrderByCreatedDesc(itemIds)
                        .stream()
                        .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        Map<Long, List<Booking>> bookingsByItems =
                bookingRepository.getAllByItemIdIn(itemIds)
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        return items
                .stream()
                .map(item -> itemMapper.toDto(
                        item,
                        commentsByItems.get(item.getId()),
                        findLastBooking(bookingsByItems.get(item.getId()), now),
                        findNextBooking(bookingsByItems.get(item.getId()), now)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableWithText(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.getAllAvailableWithText(text)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(long ownerId, ItemUpdateDto dto) {
        User owner = userRepository.require(ownerId);

        Item archetype = new Item();
        archetype.setName(dto.getName());
        archetype.setDescription(dto.getDescription());
        archetype.setAvailable(dto.getAvailable());
        archetype.setOwner(owner);
        archetype.setRequestId(dto.getRequestId());

        Item created = itemRepository.save(archetype);
        log.info(
                "Item {} owned by user #{} was successfully added with id {}",
                created.getName(),
                ownerId,
                created.getId()
        );
        return itemMapper.toDto(created);
    }

    @Override
    public ItemDto update(long callerId, long id, ItemUpdateDto dto) {
        userRepository.require(callerId);
        Item toUpdate = itemRepository.require(id);
        if (!Objects.equals(callerId, toUpdate.getOwner().getId())) {
            throw new ForbiddenAccessException("Unauthorized access to item #" + id);
        }

        if (StringUtils.isNotBlank(dto.getName())) {
            toUpdate.setName(dto.getName());
        }
        if (StringUtils.isNotBlank(dto.getDescription())) {
            toUpdate.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            toUpdate.setAvailable(dto.getAvailable());
        }

        Item updated = itemRepository.save(toUpdate);
        log.info("Item #{} was successfully updated by user #{}", updated.getId(), callerId);
        return itemMapper.toDto(updated);
    }

    @Override
    public CommentDto addComment(long authorId, long id, CommentUpdateDto dto) {
        if (!isUserRealBooker(authorId, id)) {
            throw new NotRealBookerException("Comment author is not the real booker of the item");
        }

        User author = userRepository.require(authorId);
        Item item = itemRepository.require(id);

        Comment archetype = new Comment();
        archetype.setText(dto.getText());
        archetype.setAuthor(author);
        archetype.setItem(item);
        archetype.setCreated(LocalDateTime.now());

        Comment created = commentRepository.save(archetype);
        log.info(
                "Comment #{} about item #{} from user #{} was successfully added",
                created.getId(),
                authorId,
                id
        );
        return commentMapper.toDto(created);
    }

    private boolean isUserRealBooker(long userId, long itemId) {
        Integer count = bookingRepository.getFinishedCount(userId, itemId, LocalDateTime.now());
        return (count != null) && (count > 0);
    }

    private Booking findLastBooking(List<Booking> bookings, LocalDateTime now) {
        if (bookings == null) {
            return null;
        }
        return bookings
                .stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    private Booking findNextBooking(List<Booking> bookings, LocalDateTime now) {
        if (bookings == null) {
            return null;
        }
        return bookings
                .stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}
