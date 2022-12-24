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

    List<Item> findAllByOwnerIdOrderByIdAsc(Long userId);

    @Query(
            "SELECT it FROM Item it " +
            "WHERE ( " +
                "UPPER(it.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
                "OR UPPER(it.description) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            ") AND it.available = true"
    )
    List<Item> findAllAvailableByText(String text);
}
