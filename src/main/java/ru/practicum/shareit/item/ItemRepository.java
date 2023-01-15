package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    default Item require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the item #" + id));
    }

    List<Item> getAllByOwnerId(long userId, Pageable pageable);

    @Query(
            "SELECT i FROM Item i " +
            "WHERE (" +
                "UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
                "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            ") AND i.available = true"
    )
    List<Item> getAllAvailableWithText(String text, Pageable pageable);

    List<Item> getAllByRequestId(long requestId);

    List<Item> getAllByRequestIdIn(Collection<Long> requestIds);
}
