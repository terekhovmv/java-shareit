package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    public final UserService service;
    @Autowired
    private final UserMapper mapper;

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        return mapper.toUserDto(
                service.findById(id)
        );
    }

    @GetMapping
    public List<UserDto> findAll() {
        return service
                .findAll()
                .stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto archetypeDto) {
        return mapper.toUserDto(
                service.create(mapper.toUser(archetypeDto))
        );
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto patchDto) {
        return mapper.toUserDto(
                service.update(id, mapper.toUser(patchDto))
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}