package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto get(long id);

    List<UserDto> getAll();

    UserDto create(UserUpdateDto dto);

    UserDto update(long id, UserUpdateDto dto);

    void delete(long id);
}
