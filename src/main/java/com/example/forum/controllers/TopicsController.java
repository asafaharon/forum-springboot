package com.example.forum.controllers;

import com.example.forum.entities.Topic;
import com.example.forum.entities.PublicMessage;
import com.example.forum.repositories.TopicRepository;
import com.example.forum.repositories.PublicMessageRepository;
import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/topics")
public class TopicsController {

    private final TopicRepository topicRepository;
    private final PublicMessageRepository publicMessageRepository;

    public TopicsController(TopicRepository topicRepository,
                            PublicMessageRepository publicMessageRepository) {
        this.topicRepository = topicRepository;
        this.publicMessageRepository = publicMessageRepository;
    }

    /**
     * הוספת 10 נושאי ברירת מחדל לאחר יצירת ה-Controller.
     * לפני ההוספה, מוחקים כל נושא ריק.
     */
    @PostConstruct
    public void initDefaultTopics() {

        // מחיקת נושאים ריקים - הודות לקסקדה, ההודעות יימחקו אוטומטית
        List<Topic> emptyTopics = topicRepository.findEmptyTopics();
        if (!emptyTopics.isEmpty()) {
            topicRepository.deleteAll(emptyTopics);
            System.out.println("Deleted empty topics and their messages.");
        }

        // רשימת נושאים ברירת מחדל:
        List<Topic> defaultTopics = List.of(
                new Topic("Latest Tech News", "Discussions on cutting-edge technology."),
                new Topic("Artificial Intelligence", "All about AI, machine learning, and data science."),
                new Topic("General Chat", "Casual talk about any subject."),
                new Topic("Gaming", "Video games, board games, and all things fun."),
                new Topic("Health and Fitness", "Stay fit, share your routines."),
                new Topic("Travel", "Share your travel experiences and tips."),
                new Topic("Movies & TV Shows", "Discuss the latest and greatest in entertainment."),
                new Topic("Books & Literature", "Talk about your favorite books and authors."),
                new Topic("Music", "Share and discover new music."),
                new Topic("Food & Recipes", "Exchange recipes and culinary ideas.")
        );

        for (Topic dt : defaultTopics) {
            // בדיקה האם Topic בשם הזה קיים. אם לא, נוסיף
            if (!topicRepository.existsByName(dt.getName())) {
                topicRepository.save(dt);
                System.out.println("Saved default topic: " + dt.getName());
            } else {
                System.out.println("Topic already exists: " + dt.getName());
            }
        }
    }

    @GetMapping
    public String listTopics(Model model) {
        List<Topic> topics = topicRepository.findAll();
        model.addAttribute("topics", topics);
        return "topics";
    }

    @GetMapping("/{id}/add-public-message")
    public String showAddPublicMessagePage(@PathVariable Long id, Model model) {
        model.addAttribute("topicId", id);
        return "add-public-message";
    }

    /**
     * מעדכן נושא קיים לפי ID, ואם המשתמש המחובר הוא אדמין,
     * קובע את שדה editor ל-"admin".
     */
    public void updateTopic(Topic updatedTopic, Authentication authentication) {
        // בדיקה שהנושא שהתקבל כולל מזהה
        if (updatedTopic.getId() == null) {
            throw new IllegalArgumentException("Topic ID must not be null");
        }

        // שליפה מהמאגר
        Topic existingTopic = topicRepository.findById(updatedTopic.getId())
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        // עדכון שדות בסיסיים
        existingTopic.setName(updatedTopic.getName());
        existingTopic.setDescription(updatedTopic.getDescription());

        // עדכון שדה editor עם שם המשתמש המחובר, או "anonymous" אם לא מחובר
        if (authentication != null) {
            existingTopic.setEditor(authentication.name());
        } else {
            existingTopic.setEditor("anonymous");
        }

        // שמירה במסד הנתונים
        topicRepository.save(existingTopic);
    }

    @PostMapping("/{id}/add-public-message")
    public String addPublicMessage(@PathVariable Long id, @RequestParam("content") String content) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        PublicMessage message = new PublicMessage();
        message.setContent(content);
        message.setTopic(topic);

        publicMessageRepository.save(message);

        return "redirect:/topics/" + id;
    }

    @GetMapping("/{id}")
    public String topicDetails(@PathVariable Long id, Model model) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        List<PublicMessage> messages = publicMessageRepository
                .findByTopicAndDeletedFalseOrderByCreatedAtAsc(topic);

        model.addAttribute("topic", topic);
        model.addAttribute("messages", messages);
        return "topic-details";
    }

    @GetMapping("/new")
    public String showCreateTopicForm(Model model) {
        model.addAttribute("topic", new Topic());
        return "topic-form";
    }

    // === הסרת מחיקת נושא ו"מחיקת כל" מה-TopicsController ===
    // פעולות אלו זמינות רק ל-Admin ב-AdminTopicController

    @PostMapping("/new")
    public String createTopic(@ModelAttribute Topic topic, Model model) {
        // בדיקה אם שם הנושא כבר קיים
        if (topicRepository.existsByName(topic.getName())) {
            model.addAttribute("error", "Topic with this name already exists.");
            return "topic-form";
        }

        // שמירת הנושא בבסיס הנתונים
        topicRepository.save(topic);
        System.out.println("Created new topic: " + topic.getName());

        return "redirect:/topics"; // חזרה לדף רשימת הנושאים
    }

}
