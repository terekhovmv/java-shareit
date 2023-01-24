package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.RandomAccessPageRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static ru.practicum.shareit.item.ItemAssertions.assertItemNotFound;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository testee;

    @Test
    void requireForExistent() {
        User owner = userRepository.save(
                new User(null, "name", "name@abc.def")
        );
        Item created = testee.save(
                new Item(null, "name", "description", true, owner, null)
        );

        assertEquals(
                created,
                testee.require(created.getId())
        );
    }

    @Test
    void requireForAbsent() {
        assertItemNotFound(
                Long.MAX_VALUE,
                () -> testee.require(Long.MAX_VALUE)
        );
    }

    @Test
    void getAllAvailableWithText() {
        User owner = userRepository.save(
                new User(null, "name", "name@abc.def")
        );
        Item album = addItem(owner, "album_ABC");
        addItem(owner, "banjo");
        Item capri = addItem(owner, "capri_abc");
        Item dwarf = addItem(owner, "ABC_dwarf");
        addItem(owner, "ebook");
        Item fader = addItem(owner, "abc_fader");
        Item groot = addItem(owner, "groot_ABC_groot");
        Item heels = addItem(owner, "HEELS_abc_HEELS");

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        assertIterableEquals(
                testee.getAllAvailableWithText(
                        "abc",
                        RandomAccessPageRequest.of(0, 100, sort)
                ),
                List.of(heels, groot, fader, dwarf, capri, album)
        );

        assertIterableEquals(
                testee.getAllAvailableWithText(
                        "ABC",
                        RandomAccessPageRequest.of(1, 4, sort)
                ),
                List.of(groot, fader, dwarf, capri)
        );
    }

    private Item addItem(User owner, String name) {
        return testee.save(
                new Item(null, name, name + "description", true, owner, null)
        );
    }
}
