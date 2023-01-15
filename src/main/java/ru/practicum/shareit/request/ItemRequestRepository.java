package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    default ItemRequest require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the item request #" + id));
    }

    List<ItemRequest> getAllByRequesterIdOrderByCreatedDesc(long userId);

    List<ItemRequest> getAllByRequesterIdNot(long userId, Pageable pageable);
}
