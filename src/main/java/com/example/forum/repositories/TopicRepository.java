package com.example.forum.repositories;

import com.example.forum.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByName(String name);
    void deleteAllByNameIn(List<String> names);

    /**
     * מציאת כל הנושאים שבהם השם הוא NULL או ריק.
     *
     * @return רשימת נושאים ריקים
     */
    @Query("SELECT t FROM Topic t WHERE t.name IS NULL OR t.name = ''")
    List<Topic> findEmptyTopics();
}
