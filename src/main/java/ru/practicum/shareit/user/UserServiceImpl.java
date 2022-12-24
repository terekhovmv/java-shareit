package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
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
    public UserDto findById(long id) {
        return userMapper.toUserDto(
                userRepository.require(id)
        );
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto archetypeDto) {
        User archetype = userMapper.toUser(archetypeDto);
        archetype.setId(null);

        User created = userRepository.save(archetype);
        log.info("User {} was successfully added with id {}", created.getName(), created.getId());
        return userMapper.toUserDto(created);
    }

    @Override
    public UserDto update(long id, UserDto patchDto) {
        User toUpdate = userRepository.require(id);
        userMapper.patch(patchDto, toUpdate);

        User updated = userRepository.save(toUpdate);
        log.info("User #{} was successfully updated", updated.getId());
        return userMapper.toUserDto(updated);
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
        log.info("User #{} was successfully updated", id);
    }
}