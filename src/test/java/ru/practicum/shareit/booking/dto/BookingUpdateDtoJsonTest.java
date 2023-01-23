package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingUpdateDtoJsonTest {
    @Autowired
    private JacksonTester<BookingUpdateDto> json;

    @Test
    void deserialization() throws IOException {
        String from =
                "{\"itemId\":1,\"start\":\"2030-01-01T01:02:03\",\"end\":\"2031-01-01T01:02:03\"}";

        BookingUpdateDto expected = new BookingUpdateDto();
        expected.setItemId(1l);
        expected.setStart(LocalDateTime.of(2030, 1, 1, 1, 2, 3));
        expected.setEnd(LocalDateTime.of(2031, 1, 1, 1, 2, 3));

        assertEquals(expected, json.parseObject(from));
    }
}