package ru.practicum.shareit.gateway.user;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient client;

    public UserController(UserClient client) {
        this.client = client;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable long id) {
        return client.get(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return client.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserUpdateDto dto) {
        return client.create(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody UserUpdateDto dto) {
        return client.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        client.delete(id);
    }

}
