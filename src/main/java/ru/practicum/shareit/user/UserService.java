package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserStorage storage;

    public User findById(long id) {
        return storage.findById(id);
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    public User create(User archetype) {
        User created = storage.create(archetype);
        log.info("User {} was successfully added with id {}", created.getName(), created.getId());
        return created;
    }

    public User update(long id, User patch) {
        User updated = storage.update(id, patch);
        log.info("User #{} was successfully updated", updated.getId());
        return updated;
    }

    public void delete(long id) {
        storage.delete(id);
        log.info("User #{} was successfully updated", id);
    }
}