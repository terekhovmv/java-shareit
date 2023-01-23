package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.NotRealBookerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplCommentTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    private CommentMapper commentMapper;

    private ItemServiceImpl testee;

    private Item item;

    private User author;

    private Comment comment;

    @BeforeEach
    void beforeEach() {
        commentMapper = new CommentMapper();
        testee = new ItemServiceImpl(
                userRepository,
                itemRequestRepository,
                itemRepository,
                commentRepository,
                bookingRepository,
                new ItemMapper(commentMapper),
                commentMapper
        );
        item = createItem(1, createUser(1));
        author = createUser(2);
        comment = createComment(1, "text", item, author);
    }

    @Test
    void addCommentFromNotBooker() {
        when(
                bookingRepository.getFinishedCount(eq(author.getId()), eq(item.getId()), any())
        ).thenReturn(0);

        assertThrows(
                NotRealBookerException.class,
                () -> testee.addComment(
                        author.getId(),
                        item.getId(),
                        ItemServiceTestHelper.makeCommentUpdateDto("text")
                )
        );
    }

    @Test
    void addCommentFromUnknownBooker() {
        when(
                bookingRepository.getFinishedCount(eq(author.getId()), eq(item.getId()), any())
        ).thenReturn(1);

        when(
                userRepository.require(eq(author.getId()))
        ).thenThrow(NotFoundException.class);


        assertThrows(
                NotFoundException.class,
                () -> testee.addComment(
                        author.getId(),
                        item.getId(),
                        ItemServiceTestHelper.makeCommentUpdateDto("text")
                )
        );
    }

    @Test
    void addCommentForUnknownItem() {
        when(
                bookingRepository.getFinishedCount(eq(author.getId()), eq(item.getId()), any())
        ).thenReturn(1);

        when(
                userRepository.require(eq(author.getId()))
        ).thenReturn(author);

        when(
                itemRepository.require(eq(item.getId()))
        ).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> testee.addComment(
                        author.getId(),
                        item.getId(),
                        ItemServiceTestHelper.makeCommentUpdateDto("text")
                )
        );
    }

    @Test
    void addComment() {
        when(
                bookingRepository.getFinishedCount(eq(author.getId()), eq(item.getId()), any())
        ).thenReturn(1);

        when(
                userRepository.require(eq(author.getId()))
        ).thenReturn(author);

        when(
                itemRepository.require(eq(item.getId()))
        ).thenReturn(item);

        when(
                commentRepository.save(any())
        ).thenReturn(comment);


        assertEquals(
                commentMapper.toDto(comment),
                testee.addComment(
                        author.getId(),
                        item.getId(),
                        ItemServiceTestHelper.makeCommentUpdateDto(comment.getText())
                )
        );
    }

    private User createUser(long id) {
        User result = new User();
        result.setId(id);
        result.setName("user-" + id);
        result.setEmail("user" + id + "@abc.def");
        return result;
    }

    private Item createItem(long id, User owner) {
        Item result = new Item();
        result.setId(id);
        result.setName("item-" + id);
        result.setDescription("item-description-" + id);
        result.setAvailable(true);
        result.setOwner(owner);
        return result;
    }

    private Comment createComment(long id, String text, Item item, User author) {
        Comment result = new Comment();
        result.setId(id);
        result.setText(text);
        result.setItem(item);
        result.setAuthor(author);
        result.setCreated(LocalDateTime.now());
        return result;
    }
}
