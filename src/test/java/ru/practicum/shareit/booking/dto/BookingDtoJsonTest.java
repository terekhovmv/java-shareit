package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serialization() throws IOException {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1l);
        itemDto.setName("item");
        itemDto.setDescription("item-description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(777l);
        itemDto.setComments(List.of());

        UserDto bookerDto = new UserDto();
        bookerDto.setId(1l);
        bookerDto.setName("booker");
        bookerDto.setEmail("booker@abc.def");

        BookingDto from = new BookingDto();
        from.setId(1l);
        from.setStart(LocalDateTime.of(2030, 1, 1, 1, 2, 3));
        from.setEnd(LocalDateTime.of(2031, 1, 1, 1, 2, 3));
        from.setItem(itemDto);
        from.setBooker(bookerDto);
        from.setStatus(BookingStatus.APPROVED);

        String expected =
                "{\"id\":1,\"start\":\"2030-01-01T01:02:03\",\"end\":\"2031-01-01T01:02:03\"," +
                        "\"item\":{\"id\":1,\"name\":\"item\",\"description\":\"item-description\"," +
                        "\"available\":true,\"requestId\":777,\"comments\":[],\"lastBooking\":null,\"nextBooking\":null}," +
                        "\"booker\":{\"id\":1,\"name\":\"booker\",\"email\":\"booker@abc.def\"},\"status\":\"APPROVED\"}";

        assertEquals(expected, json.write(from).getJson());
    }
}