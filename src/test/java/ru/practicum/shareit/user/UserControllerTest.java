package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UserController.class
})
public class UserControllerTest {
    @MockBean
    private UserService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void getById() throws Exception {
        UserDto user = createUserDto(1);
        when(
                service.get(user.getId())
        ).thenReturn(user);

        mvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void getAll() throws Exception {
        UserDto ann = createUserDto(1);
        UserDto bob = createUserDto(2);
        when(
                service.getAll()
        ).thenReturn(List.of(ann, bob));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(ann.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(ann.getName())))
                .andExpect(jsonPath("$.[0].email", is(ann.getEmail())))
                .andExpect(jsonPath("$.[1].id", is(bob.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(bob.getName())))
                .andExpect(jsonPath("$.[1].email", is(bob.getEmail())));
    }

    @Test
    void create() throws Exception {
        UserDto user = createUserDto(1);
        UserUpdateDto dto = toUserUpdateDto(user);

        when(
                service.create(dto)
        ).thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void update() throws Exception {
        UserDto user = createUserDto(1);
        UserUpdateDto dto = toUserUpdateDto(user);

        when(
                service.update(user.getId(), dto)
        ).thenReturn(user);

        mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void deleteById() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    private UserDto createUserDto(int idx) {
        UserDto result = new UserDto();
        result.setId((long) idx);
        result.setName("user-" + idx);
        result.setEmail("user-" + idx + "@abc.def");
        return result;
    }

    private UserUpdateDto toUserUpdateDto(UserDto from) {
        return new UserUpdateDto(from.getName(), from.getEmail());
    }
}
