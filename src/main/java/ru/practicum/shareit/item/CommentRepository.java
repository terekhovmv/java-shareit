package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getAllByItemIdOrderByCreatedDesc(long itemId);

    List<Comment> getAllByItemIdInOrderByCreatedDesc(Collection<Long> itemIds);
}
