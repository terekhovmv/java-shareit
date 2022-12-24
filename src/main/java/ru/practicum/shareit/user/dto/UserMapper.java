package ru.practicum.shareit.user.dto;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public final class UserMapper {
    public UserDto toUserDto(User from) {
        UserDto mapped = new UserDto();
        mapped.setId(from.getId());
        mapped.setName(from.getName());
        mapped.setEmail(from.getEmail());
        return mapped;
    }

    public User toUser(UserDto from) {
        User mapped = new User();
        mapped.setId(from.getId());
        mapped.setName(from.getName());
        mapped.setEmail(from.getEmail());
        return mapped;
    }

    public void patch(UserDto patch, User applyTo) {
        if (StringUtils.isNotBlank(patch.getName())) {
            applyTo.setName(patch.getName());
        }

        if (StringUtils.isNotBlank(patch.getEmail())) {
            applyTo.setEmail(patch.getEmail());
        }
    }
}
