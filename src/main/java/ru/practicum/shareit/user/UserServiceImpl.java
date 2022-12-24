package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findById(long id) {
        return userRepository.require(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User create(User archetype) {
        User created = userRepository.save(archetype);
        log.info("User {} was successfully added with id {}", created.getName(), created.getId());
        return created;
    }

    @Override
    public User update(long id, User patch) {
        User toUpdate = userRepository.require(id);

        if (StringUtils.isNotBlank(patch.getName())) {
            toUpdate.setName(patch.getName());
        }

        if (StringUtils.isNotBlank(patch.getEmail())) {
            toUpdate.setEmail(patch.getEmail());
        }

        User updated = userRepository.save(toUpdate);
        log.info("User #{} was successfully updated", updated.getId());
        return updated;
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
        log.info("User #{} was successfully updated", id);
    }
}