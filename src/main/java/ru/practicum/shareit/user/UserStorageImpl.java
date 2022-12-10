package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DataConflictException;
import ru.practicum.shareit.user.events.OnDeleteUserEvent;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    private final Map<Long, User> storage = new HashMap<>();
    private long lastId = 1;

    @Override
    public boolean contains(long id) {
        return storage.containsKey(id);
    }

    @Override
    public User findById(long id) {
        requireContains(id);
        return storage.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User create(User archetype) {
        requireUnknownEmail(archetype.getEmail(), null);

        long id = lastId++;

        User item = new User();
        item.setId(id);
        item.setName(archetype.getName());
        item.setEmail(archetype.getEmail());

        storage.put(id, item);
        return item;
    }

    @Override
    public User update(long id, User patch) {
        User existent = findById(id);

        User updated = new User();
        updated.setId(id);

        String patchName = patch.getName();
        if ((patchName == null) || patchName.isBlank()) {
            updated.setName(existent.getName());
        } else {
            updated.setName(patchName);
        }

        String patchEmail = patch.getEmail();
        if ((patchEmail == null) || patchEmail.isBlank()) {
            updated.setEmail(existent.getEmail());
        } else {
            requireUnknownEmail(patchEmail, id);
            updated.setEmail(patchEmail);
        }

        storage.put(id, updated);
        return updated;
    }

    @Override
    public void delete(long id) {
        requireContains(id);
        storage.remove(id);

        eventPublisher.publishEvent(new OnDeleteUserEvent(id));
    }

    private void requireUnknownEmail(String email, Long onlyHolder) {
        if (storage
                .values()
                .stream()
                .filter(user -> {
                    if (!user.getEmail().equalsIgnoreCase(email)) {
                        return false;
                    }
                    return (onlyHolder == null) || !Objects.equals(user.getId(), onlyHolder);
                })
                .findFirst()
                .isEmpty()
        ) {
            return;
        }

        throw new DataConflictException(String.format("The user with the email %s has been already registered", email));
    }
}
