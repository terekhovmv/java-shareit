package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItAppConsts;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUpdateDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        ItemRequestController.class
})
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void create() throws Exception {
        long callerId = 100;
        ItemRequestDto request = createItemRequestDto(1);
        ItemRequestUpdateDto dto = toItemRequestUpdateDto(request);

        when(
                service.create(callerId, dto)
        ).thenReturn(request);

        mvc.perform(post("/requests")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.created", is(dateTimeToString(request.getCreated()))))
                .andExpect(jsonPath("$.items.length()", is(request.getItems().size())));
    }

    @Test
    void getById() throws Exception {
        long callerId = 100;
        ItemRequestDto request = createItemRequestDto(1);

        when(
                service.get(callerId, request.getId())
        ).thenReturn(request);

        mvc.perform(get("/requests/" + request.getId())
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.created", is(dateTimeToString(request.getCreated()))))
                .andExpect(jsonPath("$.items.length()", is(request.getItems().size())));
    }

    @Test
    void getCreated() throws Exception {
        long callerId = 100;
        ItemRequestDto album = createItemRequestDto(0);
        ItemRequestDto banjo = createItemRequestDto(1);

        when(
                service.getCreated(callerId)
        ).thenReturn(List.of(album, banjo));

        mvc.perform(get("/requests")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(album.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(album.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(dateTimeToString(album.getCreated()))))
                .andExpect(jsonPath("$.[0].items.length()", is(album.getItems().size())))
                .andExpect(jsonPath("$.[1].id", is(banjo.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(banjo.getDescription())))
                .andExpect(jsonPath("$.[1].created", is(dateTimeToString(banjo.getCreated()))))
                .andExpect(jsonPath("$.[1].items.length()", is(banjo.getItems().size())));
    }

    @Test
    void getFromOtherUsers() throws Exception {
        long callerId = 100;
        ItemRequestDto album = createItemRequestDto(0);
        ItemRequestDto banjo = createItemRequestDto(1);

        when(
                service.getFromOtherUsers(eq(callerId), any())
        ).thenReturn(List.of(album, banjo));

        mvc.perform(get("/requests/all")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("from", "0")
                        .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(album.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(album.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(dateTimeToString(album.getCreated()))))
                .andExpect(jsonPath("$.[0].items.length()", is(album.getItems().size())))
                .andExpect(jsonPath("$.[1].id", is(banjo.getId()), Long.class))
                .andExpect(jsonPath("$.[1].description", is(banjo.getDescription())))
                .andExpect(jsonPath("$.[1].created", is(dateTimeToString(banjo.getCreated()))))
                .andExpect(jsonPath("$.[1].items.length()", is(banjo.getItems().size())));
    }

    @Test
    void getFromOtherUsersWithNotValidFrom() throws Exception {
        long callerId = 100;

        when(
                service.getFromOtherUsers(eq(callerId), any())
        ).thenReturn(List.of());

        mvc.perform(get("/requests/all")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("from", "-1")
                        .param("size", "2")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFromOtherUsersWithNotValidSize() throws Exception {
        long callerId = 100;

        when(
                service.getFromOtherUsers(eq(callerId), any())
        ).thenReturn(List.of());

        mvc.perform(get("/requests/all")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("from", "0")
                        .param("size", "0")
                )
                .andExpect(status().isBadRequest());

        mvc.perform(get("/requests/all")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("from", "0")
                        .param("size", "-1")
                )
                .andExpect(status().isBadRequest());
    }

    private ItemRequestDto createItemRequestDto(int idx) {
        ItemRequestDto result = new ItemRequestDto();
        result.setId((long) idx);
        result.setDescription("description-" + idx);
        result.setCreated(LocalDateTime.of(1984 + idx, Month.DECEMBER, 31, 23, 59, 59));
        result.setItems(List.of());
        return result;
    }

    private ItemRequestUpdateDto toItemRequestUpdateDto(ItemRequestDto from) {
        ItemRequestUpdateDto result = new ItemRequestUpdateDto();
        result.setDescription(from.getDescription());
        return result;
    }

    private String dateTimeToString(LocalDateTime from) {
        return DateTimeFormatter.ISO_DATE_TIME.format(from);
    }
}
