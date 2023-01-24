package ru.practicum.shareit.request;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.pagination.RandomAccessPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceTestHelper;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.request.ItemRequestAssertions.assertItemRequestNotFound;
import static ru.practicum.shareit.request.ItemRequestServiceTestHelper.makeItemRequestUpdateDto;
import static ru.practicum.shareit.user.UserAssertions.assertUserNotFound;

@Transactional
@SpringBootTest
public class ItemRequestServiceImplTest {
    private static final long UNKNOWN_USER_ID = Long.MAX_VALUE;

    private static final long UNKNOWN_ID = Long.MAX_VALUE;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestServiceImpl testee;

    @Autowired
    private EntityManager em;

    private UserServiceTestHelper userHelper;

    private ItemRequestServiceTestHelper requestHelper;

    @BeforeEach
    void beforeEach() {
        if (userHelper == null) {
            userHelper = new UserServiceTestHelper(userService);
        }

        if (requestHelper == null) {
            requestHelper = new ItemRequestServiceTestHelper(testee);
        }
    }

    @Test
    void create() {
        long requesterId = userHelper.createAndGetId("requester");
        ItemRequestUpdateDto dto = makeItemRequestUpdateDto("request");
        LocalDateTime before = LocalDateTime.now();

        ItemRequestDto result = testee.create(requesterId, dto);

        ItemRequest stored = em
                .createQuery(
                        "SELECT r FROM ItemRequest r WHERE r.description = :description AND r.requester.id = :requesterId",
                        ItemRequest.class
                )
                .setParameter("description", dto.getDescription())
                .setParameter("requesterId", requesterId)
                .getSingleResult();

        assertEquals(stored.getId(), result.getId());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(stored.getCreated(), result.getCreated());

        assertFalse(result.getCreated().isBefore(before));
        assertFalse(result.getCreated().isAfter(LocalDateTime.now()));
    }

    @Test
    void createByUnknownRequester() {
        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.create(
                        UNKNOWN_USER_ID,
                        makeItemRequestUpdateDto("ignore")
                )
        );
    }

    @Test
    void getById() {
        long callerId = userHelper.createAndGetId("caller");
        long requesterId = userHelper.createAndGetId("requester");

        requestHelper.create(requesterId, "album");
        ItemRequestDto banjo = requestHelper.create(requesterId, "banjo");
        requestHelper.create(requesterId, "capri");

        assertEquals(banjo, testee.get(callerId, banjo.getId()));
    }

    @Test
    void getByIdByUnknownCaller() {
        long requestId = requestHelper.createAndGetId(
                userHelper.createAndGetId("requester"),
                "ignore"
        );

        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.get(
                        UNKNOWN_USER_ID,
                        requestId
                )
        );
    }

    @Test
    void getByUnknownId() {
        long callerId = userHelper.createAndGetId("caller");

        assertItemRequestNotFound(
                UNKNOWN_ID,
                () -> testee.get(
                        callerId,
                        UNKNOWN_ID
                )
        );
    }

    @Test
    void getCreated() {
        long callerId = userHelper.createAndGetId("caller");
        long otherUserId = userHelper.createAndGetId("other");

        ItemRequestDto album = requestHelper.create(callerId, "album");
        requestHelper.create(otherUserId, "banjo");
        ItemRequestDto capri = requestHelper.create(callerId, "capri");

        assertThat(
                testee.getCreated(callerId),
                Matchers.containsInAnyOrder(album, capri)
        );
    }

    @Test
    void getCreatedByUnknownCreator() {
        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.getCreated(UNKNOWN_USER_ID)
        );
    }

    @Test
    void getFromOtherUsers() {
        long callerId = userHelper.createAndGetId("caller");
        long otherUserId = userHelper.createAndGetId("other");

        ItemRequestDto album = requestHelper.create(otherUserId, "album");
        requestHelper.create(callerId, "banjo");
        ItemRequestDto capri = requestHelper.create(otherUserId, "capri");
        ItemRequestDto dwarf = requestHelper.create(otherUserId, "dwarf");
        requestHelper.create(callerId, "ebook");
        ItemRequestDto fader = requestHelper.create(otherUserId, "fader");

        Sort sort = Sort.by(Sort.Direction.DESC, "description");

        assertIterableEquals(
                testee.getFromOtherUsers(
                        callerId,
                        RandomAccessPageRequest.of(0, 100, sort)
                ),
                List.of(fader, dwarf, capri, album)
        );

        assertIterableEquals(
                testee.getFromOtherUsers(
                        callerId,
                        RandomAccessPageRequest.of(1, 2, sort)
                ),
                List.of(dwarf, capri)
        );
    }

    @Test
    void getFromOtherUsersByUnknownCaller() {
        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.getFromOtherUsers(UNKNOWN_USER_ID, RandomAccessPageRequest.of(0, 1, Sort.unsorted()))
        );
    }
}