package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto findById(long id);

    List<UserDto> findAll();

    UserDto create(UserDto archetypeDto);

    UserDto update(long id, UserDto patchDto);

    void delete(long id);
}
