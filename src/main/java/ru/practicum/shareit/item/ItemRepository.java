package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

public interface ItemRepository extends JpaRepository<Item, Long> {
    default Item require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the item #" + id));
    }

    default Item requireAuthorized(User requester, long id) {
        Item found = require(id);
        if (!Objects.equals(requester.getId(), found.getOwner().getId())) {
            throw new ForbiddenAccessException("Unauthorized to access item #" + id);
        }
        return found;
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
