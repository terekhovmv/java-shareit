package ru.practicum.shareit.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.client.BaseClient;
import ru.practicum.shareit.gateway.item.dto.CommentUpdateDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> get(long callerId, long id) {
        return get("/" + id, callerId);
    }

    public ResponseEntity<Object> getOwned(long callerId, int from, int size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size
        );
        return get("", callerId, params);
    }

    public ResponseEntity<Object> getAvailableWithText(long callerId, String text, int from, int size) {
        Map<String, Object> params = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", callerId, params);
    }

    public ResponseEntity<Object> create(long callerId, ItemUpdateDto dto) {
        return post("/", callerId, dto);
    }

    public ResponseEntity<Object> update(long callerId, long id, ItemUpdateDto dto) {
        return patch("/" + id, callerId, dto);
    }

    public ResponseEntity<Object> addComment(long callerId, long id, CommentUpdateDto dto) {
        return post("/" + id + "/comment", callerId, dto);
    }
}
