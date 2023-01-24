package ru.practicum.shareit.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.user.UserAssertions.assertUserNotFound;
import static ru.practicum.shareit.user.UserServiceTestHelper.makeUserUpdateDto;

@Transactional
@SpringBootTest
public class UserServiceImplTest {
    private static final long UNKNOWN_ID = Long.MAX_VALUE;

    @Autowired
    private UserServiceImpl testee;

    @Autowired
    private EntityManager em;

    private UserServiceTestHelper userHelper;

    @BeforeEach
    void beforeEach() {
        if (userHelper == null) {
            userHelper = new UserServiceTestHelper(testee);
        }
    }

    @Test
    void create() {
        UserUpdateDto dto = makeUserUpdateDto("user");

        UserDto result = testee.create(dto);

        User stored = em
                .createQuery(
                        "SELECT u FROM User u WHERE u.name = :name AND u.email = :email",
                        User.class
                )
                .setParameter("name", dto.getName())
                .setParameter("email", dto.getEmail())
                .getSingleResult();

        assertEquals(stored.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getEmail(), result.getEmail());
    }

    @Test
    void getById() {
        userHelper.create("ann");
        UserDto bob = userHelper.create("bob");
        userHelper.create("cam");

        assertEquals(bob, testee.get(bob.getId()));
    }

    @Test
    void getByUnknownId() {
        assertUserNotFound(
                UNKNOWN_ID,
                () -> testee.get(UNKNOWN_ID)
        );
    }

    @Test
    void getAll() {
        UserDto ann = userHelper.create("ann");
        UserDto bob = userHelper.create("bob");
        UserDto cam = userHelper.create("cam");

        assertThat(
                testee.getAll(),
                Matchers.containsInAnyOrder(ann, bob, cam)
        );
    }

    @Test
    void update() {
        UserDto user = userHelper.create("larry");
        UserUpdateDto dto = makeUserUpdateDto("lana");

        UserDto result = testee.update(user.getId(), dto);

        User stored = em
                .createQuery(
                        "SELECT u FROM User u WHERE u.id = :id",
                        User.class
                )
                .setParameter("id", user.getId())
                .getSingleResult();
        assertEquals(dto.getName(), stored.getName());
        assertEquals(dto.getEmail(), stored.getEmail());

        assertEquals(user.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getEmail(), result.getEmail());
    }

    @Test
    void updateByUnknownId() {
        assertUserNotFound(
                UNKNOWN_ID,
                () -> testee.update(
                        UNKNOWN_ID,
                        makeUserUpdateDto("ignore")
                )
        );
    }

    @Test
    void updateDoesNotUpdateNotSpecifiedName() {
        UserDto user = userHelper.create("larry");
        UserUpdateDto dto = makeUserUpdateDto(null, "lana@abc.def");

        UserDto result = testee.update(user.getId(), dto);
        String expectedName = user.getName();

        User stored = em
                .createQuery(
                        "SELECT u FROM User u WHERE u.id = :id",
                        User.class
                )
                .setParameter("id", user.getId())
                .getSingleResult();
        assertEquals(expectedName, stored.getName());
        assertEquals(dto.getEmail(), stored.getEmail());

        assertEquals(user.getId(), result.getId());
        assertEquals(expectedName, result.getName());
        assertEquals(dto.getEmail(), result.getEmail());
    }

    @Test
    void updateDoesNotUpdateNotSpecifiedEmail() {
        UserDto user = userHelper.create("larry");
        UserUpdateDto dto = makeUserUpdateDto("lana", null);

        UserDto result = testee.update(user.getId(), dto);
        String expectedEmail = user.getEmail();

        User stored = em
                .createQuery(
                        "SELECT u FROM User u WHERE u.id = :id",
                        User.class
                )
                .setParameter("id", user.getId())
                .getSingleResult();
        assertEquals(dto.getName(), stored.getName());
        assertEquals(expectedEmail, stored.getEmail());

        assertEquals(user.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(expectedEmail, result.getEmail());
    }

    @Test
    void delete() {
        long id = userHelper.create("user").getId();

        testee.delete(id);

        assertUserNotFound(
                id,
                () -> testee.get(id)
        );
    }

    @Test
    void deleteByUnknownId() {
        assertUserNotFound(
                UNKNOWN_ID,
                () -> testee.delete(UNKNOWN_ID)
        );
    }
}
