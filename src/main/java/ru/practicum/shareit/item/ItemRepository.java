package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    default Item require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the item #" + id));
    }

    @Query("SELECT i FROM Item i WHERE i.owner.id = ?1 ORDER BY i.id ASC")
    List<Item> getAllOwned(long userId);

    @Query(
            "SELECT i FROM Item i " +
            "WHERE (" +
                "UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
                "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            ") AND i.available = true"
    )
    List<Item> getAllAvailableWithText(String text);
}
