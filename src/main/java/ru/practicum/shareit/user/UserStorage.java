package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    boolean contains(long id);

    default void requireContains(long id) {
        if (!contains(id)) {
            throw new NotFoundException("Unable to find the user #" + id);
        }
    }

    User findById(long id);

    List<User> findAll();

    User create(User archetype);

    User update(long id, User patch);

    void delete(long id);
}
