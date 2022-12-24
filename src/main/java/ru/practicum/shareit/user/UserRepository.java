package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    default User require(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Unable to find the user #" + id));
    }
}
