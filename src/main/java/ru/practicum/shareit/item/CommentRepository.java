package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT cm FROM Comment cm WHERE cm.item.id = ?1 ORDER BY cm.created DESC")
    List<Comment> getItemComments(long itemId);
}
