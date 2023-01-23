package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.request.ItemRequestAssertions.assertItemRequestNotFound;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository testee;

    @Test
    void requireForExistent() {
        User requester = userRepository.save(
                new User(null, "name", "name@abc.def")
        );
        ItemRequest created = testee.save(
                new ItemRequest(null, "description", requester, LocalDateTime.now())
        );

        assertEquals(
                created,
                testee.require(created.getId())
        );
    }

    @Test
    void requireForAbsent() {
        assertItemRequestNotFound(
                Long.MAX_VALUE,
                () -> testee.require(Long.MAX_VALUE)
        );
    }
}
