package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    public final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return service.findAll();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto archetypeDto) {
        return service.create(archetypeDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto patchDto) {
        return service.update(id, patchDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}