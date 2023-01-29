package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.user.UserAssertions.assertUserNotFound;


@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository testee;

    @Test
    void requireForExistent() {
        User created = testee.save(
                new User(null, "name", "name@abc.def")
        );

        assertEquals(
                created,
                testee.require(created.getId())
        );
    }

    @Test
    void requireForAbsent() {
        assertUserNotFound(
                Long.MAX_VALUE,
                () -> testee.require(Long.MAX_VALUE)
        );
    }
}
