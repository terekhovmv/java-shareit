package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public class UserServiceTestHelper {
    private final UserService service;

    public UserServiceTestHelper(UserService service) {
        this.service = service;
    }

    public static UserUpdateDto makeUserUpdateDto(String name, String email) {
        UserUpdateDto result = new UserUpdateDto();
        result.setName(name);
        result.setEmail(email);
        return result;
    }

    public static UserUpdateDto makeUserUpdateDto(String name) {
        return makeUserUpdateDto(name, name + "@abc.def");
    }

    public UserDto create(String name) {
        return service.create(makeUserUpdateDto(name));
    }

    public long createAndGetId(String name) {
        return create(name).getId();
    }
}
