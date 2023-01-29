package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItAppConsts;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFilter;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        BookingController.class
})
public class BookingControllerTest {
    @MockBean
    private BookingService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void create() throws Exception {
        long callerId = 100;
        BookingDto booking = createBookingDto(callerId, 200, 1);
        BookingUpdateDto dto = toBookingUpdateDto(booking);

        when(
                service.create(callerId, dto)
        ).thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(dateTimeToString(booking.getStart()))))
                .andExpect(jsonPath("$.end", is(dateTimeToString(booking.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void setApproved() throws Exception {
        long callerId = 100;
        BookingDto booking = createBookingDto(callerId, 200, 1);
        boolean approved = true;

        when(
                service.setApproved(callerId, booking.getId(), approved)
        ).thenReturn(booking);

        mvc.perform(patch("/bookings/" + booking.getId() + "?approved=" + approved)
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(dateTimeToString(booking.getStart()))))
                .andExpect(jsonPath("$.end", is(dateTimeToString(booking.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void getById() throws Exception {
        long callerId = 100;
        BookingDto booking = createBookingDto(callerId, 200, 1);

        when(
                service.get(callerId, booking.getId())
        ).thenReturn(booking);

        mvc.perform(get("/bookings/" + booking.getId())
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(dateTimeToString(booking.getStart()))))
                .andExpect(jsonPath("$.end", is(dateTimeToString(booking.getEnd()))))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void getCreated() throws Exception {
        long callerId = 100;
        BookingDto album = createBookingDto(callerId, 1, 1);
        BookingDto banjo = createBookingDto(callerId, 2, 2);
        BookingFilter filter = BookingFilter.WAITING;

        when(
                service.getCreated(eq(callerId), eq(filter), any())
        ).thenReturn(List.of(album, banjo));

        mvc.perform(get("/bookings")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter.toString())
                        .param("from", "0")
                        .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(album.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(dateTimeToString(album.getStart()))))
                .andExpect(jsonPath("$.[0].end", is(dateTimeToString(album.getEnd()))))
                .andExpect(jsonPath("$.[0].item.id", is(album.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(album.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(album.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(banjo.getId()), Long.class))
                .andExpect(jsonPath("$.[1].start", is(dateTimeToString(banjo.getStart()))))
                .andExpect(jsonPath("$.[1].end", is(dateTimeToString(banjo.getEnd()))))
                .andExpect(jsonPath("$.[1].item.id", is(banjo.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[1].booker.id", is(banjo.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[1].status", is(banjo.getStatus().toString())));
    }

    @Test
    void getCreatedWithNotValidFrom() throws Exception {
        long callerId = 100;
        BookingFilter filter = BookingFilter.WAITING;

        when(
                service.getCreated(eq(callerId), any(), any())
        ).thenReturn(List.of());

        mvc.perform(get("/bookings")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter.toString())
                        .param("from", "-1")
                        .param("size", "2")
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getCreatedNotValidSize() throws Exception {
        long callerId = 100;
        BookingFilter filter = BookingFilter.WAITING;

        when(
                service.getCreated(eq(callerId), any(), any())
        ).thenReturn(List.of());

        mvc.perform(get("/bookings")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter.toString())
                        .param("from", "0")
                        .param("size", "0")
                )
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/bookings")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter.toString())
                        .param("from", "0")
                        .param("size", "-1")
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getCreatedWithNotValidFilter() throws Exception {
        long callerId = 100;
        String filter = "unknown";

        when(
                service.getCreated(eq(callerId), any(), any())
        ).thenReturn(List.of());

        mvc.perform(get("/bookings")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter)
                        .param("from", "0")
                        .param("size", "2")
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getForOwnedItems() throws Exception {
        long callerId = 100;
        BookingDto album = createBookingDto(callerId + 1, 1, 1);
        BookingDto banjo = createBookingDto(callerId + 1, 2, 2);
        BookingFilter filter = BookingFilter.WAITING;

        when(
                service.getForOwnedItems(eq(callerId), eq(filter), any())
        ).thenReturn(List.of(album, banjo));

        mvc.perform(get("/bookings/owner")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter.toString())
                        .param("from", "0")
                        .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(album.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(dateTimeToString(album.getStart()))))
                .andExpect(jsonPath("$.[0].end", is(dateTimeToString(album.getEnd()))))
                .andExpect(jsonPath("$.[0].item.id", is(album.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(album.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(album.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(banjo.getId()), Long.class))
                .andExpect(jsonPath("$.[1].start", is(dateTimeToString(banjo.getStart()))))
                .andExpect(jsonPath("$.[1].end", is(dateTimeToString(banjo.getEnd()))))
                .andExpect(jsonPath("$.[1].item.id", is(banjo.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[1].booker.id", is(banjo.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[1].status", is(banjo.getStatus().toString())));
    }

    @Test
    void getForOwnedItemsWithNotValidFrom() throws Exception {
        long callerId = 100;
        BookingFilter filter = BookingFilter.WAITING;

        when(
                service.getForOwnedItems(eq(callerId), any(), any())
        ).thenReturn(List.of());

        mvc.perform(get("/bookings/owner")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter.toString())
                        .param("from", "-1")
                        .param("size", "2")
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getForOwnedItemsNotValidSize() throws Exception {
        long callerId = 100;
        BookingFilter filter = BookingFilter.WAITING;

        when(
                service.getForOwnedItems(eq(callerId), any(), any())
        ).thenReturn(List.of());

        mvc.perform(get("/bookings/owner")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter.toString())
                        .param("from", "0")
                        .param("size", "0")
                )
                .andExpect(status().is5xxServerError());

        mvc.perform(get("/bookings/owner")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter.toString())
                        .param("from", "0")
                        .param("size", "-1")
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getForOwnedItemsWithNotValidFilter() throws Exception {
        long callerId = 100;
        String filter = "unknown";

        when(
                service.getForOwnedItems(eq(callerId), any(), any())
        ).thenReturn(List.of());

        mvc.perform(get("/bookings/owner")
                        .header(ShareItAppConsts.HEADER_CALLER_ID, callerId)
                        .param("state", filter)
                        .param("from", "0")
                        .param("size", "2")
                )
                .andExpect(status().is5xxServerError());
    }


    private BookingDto createBookingDto(long bookerId, long itemId, long bookingId) {
        UserDto booker = new UserDto();
        booker.setId(bookerId);
        //...

        ItemDto item = new ItemDto();
        item.setId(itemId);
        //...

        BookingDto result = new BookingDto();
        result.setId(bookingId);
        result.setStatus(BookingStatus.WAITING);
        result.setStart(LocalDateTime.of(2023 + (int) bookingId, Month.JANUARY, 1, 0, 0, 0));
        result.setEnd(LocalDateTime.of(2024 + (int) bookingId, Month.JANUARY, 1, 0, 0, 0));
        result.setItem(item);
        result.setBooker(booker);
        return result;
    }

    private BookingUpdateDto toBookingUpdateDto(BookingDto from) {
        BookingUpdateDto result = new BookingUpdateDto();
        result.setItemId(from.getItem().getId());
        result.setStart(from.getStart());
        result.setEnd(from.getEnd());
        return result;
    }

    private String dateTimeToString(LocalDateTime from) {
        return DateTimeFormatter.ISO_DATE_TIME.format(from);
    }
}
