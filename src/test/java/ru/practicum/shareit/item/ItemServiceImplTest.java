package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.RandomAccessPageRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestServiceTestHelper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceTestHelper;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.item.ItemAssertions.assertItemNotFound;
import static ru.practicum.shareit.item.ItemServiceTestHelper.makeItemUpdateDto;
import static ru.practicum.shareit.request.ItemRequestAssertions.assertItemRequestNotFound;
import static ru.practicum.shareit.user.UserAssertions.assertUserNotFound;

@Transactional
@SpringBootTest
public class ItemServiceImplTest {
    private static final long UNKNOWN_USER_ID = Long.MAX_VALUE;

    private static final long UNKNOWN_REQUEST_ID = Long.MAX_VALUE;

    private static final long UNKNOWN_ID = Long.MAX_VALUE;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemServiceImpl testee;

    @Autowired
    private EntityManager em;

    private UserServiceTestHelper userHelper;

    private ItemRequestServiceTestHelper requestHelper;

    private ItemServiceTestHelper itemHelper;

    @BeforeEach
    void beforeEach() {
        if (userHelper == null) {
            userHelper = new UserServiceTestHelper(userService);
        }

        if (requestHelper == null) {
            requestHelper = new ItemRequestServiceTestHelper(requestService);
        }

        if (itemHelper == null) {
            itemHelper = new ItemServiceTestHelper(testee);
        }
    }

    @Test
    void create() {
        long ownerId = userHelper.createAndGetId("owner");
        long requestId = requestHelper.createAndGetId(userHelper.createAndGetId("requester"), "request");
        ItemUpdateDto dto = makeItemUpdateDto("item", requestId);

        ItemDto result = testee.create(ownerId, dto);

        Item stored = em
                .createQuery(
                        "SELECT i FROM Item i WHERE i.owner.id = :ownerId AND i.name = :name AND i.requestId = :requestId",
                        Item.class
                )
                .setParameter("ownerId", ownerId)
                .setParameter("name", dto.getName())
                .setParameter("requestId", dto.getRequestId())
                .getSingleResult();
        assertEquals(dto.getDescription(), stored.getDescription());
        assertEquals(dto.getAvailable(), stored.getAvailable());

        assertEquals(stored.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getAvailable(), result.getAvailable());
        assertEquals(dto.getRequestId(), result.getRequestId());
    }

    @Test
    void createByUnknownOwner() {
        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.create(
                        UNKNOWN_USER_ID,
                        makeItemUpdateDto("ignore", null)
                )
        );
    }

    @Test
    void createWithUnknownRequest() {
        assertItemRequestNotFound(
                UNKNOWN_REQUEST_ID,
                () -> testee.create(
                        userHelper.createAndGetId("owner"),
                        makeItemUpdateDto("ignore", UNKNOWN_REQUEST_ID)
                )
        );
    }

    @Test
    void getById() {
        long ownerId = userHelper.createAndGetId("owner");
        long callerId = userHelper.createAndGetId("caller");

        itemHelper.create(ownerId, "album");
        ItemDto banjo = itemHelper.create(ownerId, "banjo");
        itemHelper.create(ownerId, "capri");

        assertEquals(banjo, testee.get(callerId, banjo.getId()));
    }

    @Test
    void getByIdByUnknownCaller() {
        long itemId = itemHelper.createAndGetId(
                userHelper.createAndGetId("owner"),
                "ignore",
                null
        );

        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.get(
                        UNKNOWN_USER_ID,
                        itemId
                )
        );
    }

    @Test
    void getByUnknownId() {
        long callerId = userHelper.createAndGetId("caller");

        assertItemNotFound(
                UNKNOWN_ID,
                () -> testee.get(
                        callerId,
                        UNKNOWN_ID
                )
        );
    }

    @Test
    void update() {
        long ownerId = userHelper.createAndGetId("owner");
        ItemDto item = itemHelper.create(ownerId, "name");

        long requestId = requestHelper.createAndGetId(
                userHelper.createAndGetId("requester"),
                "request"
        );
        ItemUpdateDto dto = makeItemUpdateDto(
                "new-name",
                "new-description",
                !item.getAvailable(),
                requestId
        );


        ItemDto result = testee.update(ownerId, item.getId(), dto);

        Item stored = em
                .createQuery(
                        "SELECT i FROM Item i WHERE i.id = :id",
                        Item.class
                )
                .setParameter("id", item.getId())
                .getSingleResult();
        assertEquals(dto.getName(), stored.getName());
        assertEquals(dto.getDescription(), stored.getDescription());
        assertEquals(dto.getAvailable(), stored.getAvailable());
        assertEquals(dto.getRequestId(), stored.getRequestId());

        assertEquals(item.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getAvailable(), result.getAvailable());
        assertEquals(dto.getRequestId(), result.getRequestId());
    }

    @Test
    void updateWithNoFieldsSpecified() {
        long ownerId = userHelper.createAndGetId("owner");
        ItemDto item = itemHelper.create(ownerId, "name");

        ItemUpdateDto dto = makeItemUpdateDto(
                null,
                null,
                null,
                null
        );

        ItemDto result = testee.update(ownerId, item.getId(), dto);

        Item stored = em
                .createQuery(
                        "SELECT i FROM Item i WHERE i.id = :id",
                        Item.class
                )
                .setParameter("id", item.getId())
                .getSingleResult();
        assertEquals(item.getName(), stored.getName());
        assertEquals(item.getDescription(), stored.getDescription());
        assertEquals(item.getAvailable(), stored.getAvailable());
        assertEquals(item.getRequestId(), stored.getRequestId());

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequestId(), result.getRequestId());
    }

    @Test
    void updateByUnknownCaller() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "name");

        ItemUpdateDto dto = makeItemUpdateDto("new-name", null);

        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.update(
                        UNKNOWN_USER_ID,
                        itemId,
                        dto
                )
        );
    }

    @Test
    void updateByNotOwner() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "name");

        long callerId = userHelper.createAndGetId("caller");
        ItemUpdateDto dto = makeItemUpdateDto("new-name", null);

        assertThrows(
                ForbiddenAccessException.class,
                () -> testee.update(callerId, itemId, dto)
        );
    }

    @Test
    void updateWithUnknownRequest() {
        long ownerId = userHelper.createAndGetId("owner");
        long itemId = itemHelper.createAndGetId(ownerId, "name");

        ItemUpdateDto dto = makeItemUpdateDto("new-name", UNKNOWN_REQUEST_ID);

        assertItemRequestNotFound(
                UNKNOWN_REQUEST_ID,
                () -> testee.update(ownerId, itemId, dto)
        );
    }

    @Test
    void getOwned() {
        long ownerId = userHelper.createAndGetId("owner");
        long otherUserId = userHelper.createAndGetId("other");

        ItemDto album = itemHelper.create(ownerId, "album");
        itemHelper.create(otherUserId, "banjo");
        ItemDto capri = itemHelper.create(ownerId, "capri");
        ItemDto dwarf = itemHelper.create(ownerId, "dwarf");
        itemHelper.create(otherUserId, "ebook");
        ItemDto fader = itemHelper.create(ownerId, "fader");

        Sort sort = Sort.by(Sort.Direction.DESC, "name");

        assertIterableEquals(
                testee.getOwned(
                        ownerId,
                        RandomAccessPageRequest.of(0, 100, sort)
                ),
                List.of(fader, dwarf, capri, album)
        );

        assertIterableEquals(
                testee.getOwned(
                        ownerId,
                        RandomAccessPageRequest.of(1, 2, sort)
                ),
                List.of(dwarf, capri)
        );
    }

    @Test
    void getOwnedByUnknownCaller() {
        assertUserNotFound(
                UNKNOWN_USER_ID,
                () -> testee.getOwned(
                        UNKNOWN_USER_ID,
                        RandomAccessPageRequest.of(0, 1, Sort.unsorted())
                )
        );
    }

    @Test
    void getAvailableWithText() {
        long ownerId = userHelper.createAndGetId("owner");

        ItemDto album = itemHelper.create(ownerId, "album_ABC");
        itemHelper.create(ownerId, "banjo");
        ItemDto capri = itemHelper.create(ownerId, "capri_abc");
        ItemDto dwarf = itemHelper.create(ownerId, "ABC_dwarf");
        itemHelper.create(ownerId, "ebook");
        ItemDto fader = itemHelper.create(ownerId, "abc_fader");
        ItemDto groot = itemHelper.create(ownerId, "groot_ABC_groot");
        ItemDto heels = itemHelper.create(ownerId, "HEELS_abc_HEELS");

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        assertIterableEquals(
                testee.getAvailableWithText(
                        "abc",
                        RandomAccessPageRequest.of(0, 100, sort)
                ),
                List.of(heels, groot, fader, dwarf, capri, album)
        );

        assertIterableEquals(
                testee.getAvailableWithText(
                        "ABC",
                        RandomAccessPageRequest.of(1, 4, sort)
                ),
                List.of(groot, fader, dwarf, capri)
        );
    }

    @Test
    void getAvailableWithEmptyText() {
        assertIterableEquals(
                testee.getAvailableWithText(
                        "ABC",
                        RandomAccessPageRequest.of(0, 1, Sort.unsorted())
                ),
                List.of()
        );
    }
}