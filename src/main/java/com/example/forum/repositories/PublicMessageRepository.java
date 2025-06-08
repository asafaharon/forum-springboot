package com.example.forum.repositories;

import com.example.forum.entities.PublicMessage;
import com.example.forum.entities.Topic;
import com.example.forum.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PublicMessageRepository extends JpaRepository<PublicMessage, Long> {
    List<PublicMessage> findByTopicAndDeletedFalseOrderByCreatedAtAsc(Topic topic);
    List<PublicMessage> findByCreatedAtBeforeAndDeletedFalse(LocalDateTime cutoff);
    void deleteByTopic(Topic topic);

    // מתודה למחיקת הודעות על פי המחבר
    void deleteByAuthor(User author);
}
