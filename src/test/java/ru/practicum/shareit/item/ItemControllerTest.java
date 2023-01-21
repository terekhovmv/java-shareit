package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItAppConsts;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        ItemController.class
})
public class ItemControllerTest {
    @MockBean
    private ItemService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    void getById() throws Exception {
        long callerId = 100;
        ItemDto item = createItemDto(1);

        when(
                service.get(callerId, item.getId())
        ).thenReturn(item);

        mvc.perform(get("/items/" + item.getId())
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }


    @Test
    void getOwned() throws Exception {
        long callerId = 100;
        ItemDto album = createItemDto(1);
        ItemDto banjo = createItemDto(2);

        when(
                service.getOwned(eq(callerId), any())
        ).thenReturn(List.of(album, banjo));

        mvc.perform(get("/items")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("from", "0")
                        .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(album.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(album.getName())))
                .andExpect(jsonPath("$.[0].description", is(album.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(album.getAvailable())))
                .andExpect(jsonPath("$.[1].id", is(banjo.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(banjo.getName())))
                .andExpect(jsonPath("$.[1].description", is(banjo.getDescription())))
                .andExpect(jsonPath("$.[1].available", is(banjo.getAvailable())));
    }

    @Test
    void getOwnedWithNotValidFrom() throws Exception {
        long callerId = 100;

        when(
                service.getOwned(eq(callerId), any())
        ).thenReturn(List.of());

        mvc.perform(get("/items")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("from", "-1")
                        .param("size", "2")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnedWithNotValidSize() throws Exception {
        long callerId = 100;

        when(
                service.getOwned(eq(callerId), any())
        ).thenReturn(List.of());

        mvc.perform(get("/items")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("from", "0")
                        .param("size", "0")
                )
                .andExpect(status().isBadRequest());

        mvc.perform(get("/items")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("from", "0")
                        .param("size", "-1")
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void getAvailableWithText() throws Exception {
        String text = "suitable for";
        ItemDto album = createItemDto(1);
        ItemDto banjo = createItemDto(2);

        when(
                service.getAvailableWithText(eq(text), any())
        ).thenReturn(List.of(album, banjo));

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(album.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(album.getName())))
                .andExpect(jsonPath("$.[0].description", is(album.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(album.getAvailable())))
                .andExpect(jsonPath("$.[1].id", is(banjo.getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(banjo.getName())))
                .andExpect(jsonPath("$.[1].description", is(banjo.getDescription())))
                .andExpect(jsonPath("$.[1].available", is(banjo.getAvailable())));
    }

    @Test
    void getAvailableWithTextWithNotValidFrom() throws Exception {
        String text = "suitable for";

        when(
                service.getAvailableWithText(eq(text), any())
        ).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "-1")
                        .param("size", "2")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAvailableWithTextWithNotValidSize() throws Exception {
        String text = "suitable for";

        when(
                service.getAvailableWithText(eq(text), any())
        ).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "0")
                )
                .andExpect(status().isBadRequest());

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "-1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void create() throws Exception {
        long callerId = 100;
        ItemDto item = createItemDto(1);
        ItemUpdateDto dto = toItemUpdateDto(item);

        when(
                service.create(callerId, dto)
        ).thenReturn(item);

        mvc.perform(post("/items")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void update() throws Exception {
        long callerId = 100;
        ItemDto item = createItemDto(1);
        ItemUpdateDto dto = toItemUpdateDto(item);

        when(
                service.update(callerId, item.getId(), dto)
        ).thenReturn(item);

        mvc.perform(patch("/items/" + item.getId())
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void addComment() throws Exception {
        long callerId = 100;
        long itemId = 200;
        CommentDto comment = createCommentDto(1);
        CommentUpdateDto dto = toCommentUpdateDto(comment);

        when(
                service.addComment(callerId, itemId, dto)
        ).thenReturn(comment);

        mvc.perform(post("/items/" + itemId + "/comment")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created", is(dateTimeToString(comment.getCreated()))));
    }

    private ItemDto createItemDto(long id) {
        ItemDto result = new ItemDto();
        result.setId(id);
        result.setName("name-" + id);
        result.setDescription("description-" + id);
        result.setAvailable(id % 2 == 1);
        return result;
    }

    private ItemUpdateDto toItemUpdateDto(ItemDto from) {
        ItemUpdateDto result = new ItemUpdateDto();
        result.setName(from.getName());
        result.setDescription(from.getDescription());
        result.setAvailable(from.getAvailable());
        return result;
    }

    private CommentDto createCommentDto(long id) {
        CommentDto result = new CommentDto();
        result.setId(id);
        result.setText("text-" + id);
        result.setAuthorName("author-name-" + id);
        result.setCreated(LocalDateTime.now());
        return result;
    }

    private CommentUpdateDto toCommentUpdateDto(CommentDto from) {
        CommentUpdateDto result = new CommentUpdateDto();
        result.setText(from.getText());
        return result;
    }

    private String dateTimeToString(LocalDateTime from) {
        return DateTimeFormatter.ISO_DATE_TIME.format(from);
    }
}
