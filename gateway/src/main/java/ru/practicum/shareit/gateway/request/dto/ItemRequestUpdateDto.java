package ru.practicum.shareit.gateway.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemRequestUpdateDto {
    @NotBlank
    String description;
}
