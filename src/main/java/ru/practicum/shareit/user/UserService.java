package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User findById(long id);

    List<User> findAll();

    User create(User archetype);

    User update(long id, User patch);

    void delete(long id);
}
