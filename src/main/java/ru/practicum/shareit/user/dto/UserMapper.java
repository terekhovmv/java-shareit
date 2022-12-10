package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public final class UserMapper {
    public UserDto toUserDto(User from) {
        UserDto to = new UserDto();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setEmail(from.getEmail());

        return to;
    }

    public User toUser(UserDto from) {
        User to = new User();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setEmail(from.getEmail());

        return to;
    }
}
