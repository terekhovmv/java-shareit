package ru.practicum.shareit.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.exceptions.DataConflictException;
import ru.practicum.shareit.user.events.OnDeleteUserEvent;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {
    private final ApplicationEventPublisher eventPublisher;

    private final Map<Long, User> storage = new HashMap<>();
    private long lastId = 1;

    public UserStorageImpl(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

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

        final long id = lastId++;
        User created = archetype.toBuilder()
                .id(id)
                .name(archetype.getName())
                .email(archetype.getEmail())
                .build();

        storage.put(id, created);
        return created;
    }

    @Override
    public User update(long id, User patch) {
        User.UserBuilder builder = findById(id).toBuilder();

        if (StringUtils.isNotBlank(patch.getName())) {
            builder.name(patch.getName());
        }

        if (StringUtils.isNotBlank(patch.getEmail())) {
            requireUnknownEmail(patch.getEmail(), id);
            builder.email(patch.getEmail());
        }

        User updated = builder.build();
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
        if (storage.values()
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
