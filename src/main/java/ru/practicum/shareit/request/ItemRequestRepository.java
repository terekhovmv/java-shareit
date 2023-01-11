package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT r FROM ItemRequest r WHERE r.requester.id = ?1 ORDER BY r.created DESC")
    List<ItemRequest> getCreated(long userId);
}
