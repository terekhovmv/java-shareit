package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public final class UserMapper {
    public UserDto toDto(User from) {
        UserDto mapped = new UserDto();

        mapped.setId(from.getId());
        mapped.setName(from.getName());
        mapped.setEmail(from.getEmail());

        return mapped;
    }
}
