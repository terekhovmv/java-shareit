package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto get(long id) {
        return userMapper.toDto(
                userRepository.require(id)
        );
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserUpdateDto dto) {
        User archetype = new User();
        archetype.setName(dto.getName());
        archetype.setEmail(dto.getEmail());

        User created = userRepository.save(archetype);
        log.info("User {} was successfully added with id {}", created.getName(), created.getId());
        return userMapper.toDto(created);
    }

    @Override
    public UserDto update(long id, UserUpdateDto dto) {
        User toUpdate = userRepository.require(id);

        if (StringUtils.isNotBlank(dto.getName())) {
            toUpdate.setName(dto.getName());
        }
        if (StringUtils.isNotBlank(dto.getEmail())) {
            toUpdate.setEmail(dto.getEmail());
        }

        User updated = userRepository.save(toUpdate);
        log.info("User #{} was successfully updated", updated.getId());
        return userMapper.toDto(updated);
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
        log.info("User #{} was successfully updated", id);
    }
}