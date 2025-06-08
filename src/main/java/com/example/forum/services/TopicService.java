package com.example.forum.services;

import com.example.forum.entities.Topic;
import com.example.forum.repositories.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    // שמירת נושא
    public void save(Topic topic) {
        topicRepository.save(topic);
    }
    public void updateTopic(Topic topic) {
        if (topic.getId() == null) {
            throw new IllegalArgumentException("Topic ID must not be null");
        }
        Topic existingTopic = topicRepository.findById(topic.getId())
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        existingTopic.setName(topic.getName());
        existingTopic.setDescription(topic.getDescription());
        topicRepository.save(existingTopic);
    }

    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new IllegalArgumentException("Topic with ID " + id + " does not exist.");
        }
        topicRepository.deleteById(id);
    }


    // מחיקת נושא לפי מזהה
    public void deleteById(Long id) {
        topicRepository.deleteById(id);
    }
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id).orElse(null); // מחזיר את Topic או null
    }

    public void saveTopic(Topic topic) {
        topicRepository.save(topic); // שמירה ב-DB
    }
    // מציאת נושא לפי מזהה
    public Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    // הבאת כל הנושאים
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }
}
