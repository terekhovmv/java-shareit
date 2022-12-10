package ru.practicum.shareit.user.events;

import lombok.Value;

@Value
public class OnDeleteUserEvent {
    private final long id;
}
