package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public final class UserMapper {
    public UserDto toUserDto(User from) {
        return UserDto.builder()
                .id(from.getId())
                .name(from.getName())
                .email(from.getEmail())
                .build();
    }

    public User toUser(UserDto from) {
        return User.builder()
                .id(from.getId())
                .name(from.getName())
                .email(from.getEmail())
                .build();
    }
}
