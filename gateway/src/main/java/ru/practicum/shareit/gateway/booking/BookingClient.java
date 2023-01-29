package ru.practicum.shareit.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long callerId, BookingUpdateDto dto) {
        return post("/", callerId, dto);
    }

    public ResponseEntity<Object> setApproved(long callerId, long id, boolean value) {
        Map<String, Object> params = Map.of(
                "approved", value
        );
        return patch("/" + id + "?approved={approved}", callerId, params, null);
    }

    public ResponseEntity<Object> get(long callerId, long id) {
        return get("/" + id, callerId);
    }

    public ResponseEntity<Object> getCreated(long callerId, String state, int from, int size) {
        Map<String, Object> params = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", callerId, params);
    }

    public ResponseEntity<Object> getForOwnedItems(long callerId, String state, int from, int size) {
        Map<String, Object> params = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", callerId, params);
    }
}

