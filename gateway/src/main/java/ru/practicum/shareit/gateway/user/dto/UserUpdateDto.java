package ru.practicum.shareit.gateway.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateDto {
    @NotBlank
    String name;
    @NotBlank
    @Email
    String email;
}
