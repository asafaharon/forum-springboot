package com.example.forum.controllers;

import com.example.forum.services.TopicService;
import com.example.forum.entities.Topic;
import com.example.forum.repositories.TopicRepository;
import com.example.forum.repositories.PublicMessageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/topics")
public class AdminTopicController {

    private final TopicRepository topicRepository;
    private final PublicMessageRepository publicMessageRepository;
    private final TopicService topicService;

    // נוסיף PublicMessageRepository לבנאי לצורך deleteAll
    public AdminTopicController(TopicRepository topicRepository,
                                PublicMessageRepository publicMessageRepository) {
        this.topicRepository = topicRepository;
        this.publicMessageRepository = publicMessageRepository;

        this.topicService = new TopicService(topicRepository);
    }

    @GetMapping
    public String adminTopicsList(Model model) {
        model.addAttribute("topics", topicRepository.findAll());
        return "admin-topics";
    }

    @GetMapping("/new")
    public String newTopicForm(Model model) {
        model.addAttribute("topic", new Topic());
        return "topic-form";
    }

    @PostMapping("/new")
    public String createTopic(@ModelAttribute("topic") Topic topic) {
        topicRepository.save(topic);
        return "redirect:/admin/topics";
    }

    @GetMapping("/edit/{id}")
    public String editTopic(@PathVariable Long id, Model model) {
        Topic topic = topicService.getTopicById(id);
        model.addAttribute("topic", topic);
        return "edit-topic";
    }

    @PostMapping("/{id}/edit")
    public String updateTopic(@PathVariable Long id,
                              @ModelAttribute("topic") Topic updatedTopic) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        topic.setName(updatedTopic.getName());
        topic.setDescription(updatedTopic.getDescription());
        topicRepository.save(topic);
        return "redirect:/admin/topics";
    }

    @PostMapping("/delete/{id}")
    public String deleteTopic(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            topicService.deleteTopic(id); // Call service layer to delete the topic
            redirectAttributes.addFlashAttribute("message", "Topic deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting topic: " + e.getMessage());
        }
        return "redirect:/admin/topics";
    }

    @PostMapping("/edit")
    public String saveEditedTopic(@ModelAttribute Topic topic) {
        if (topic.getId() == null) {
            throw new IllegalArgumentException("Topic ID must not be null");
        }
        topicService.updateTopic(topic);
        return "redirect:/admin/topics";
    }

    // מחיקת כל הנושאים ואפשרות למחוק גם הודעות ציבוריות - זמין רק לאדמין
    @PostMapping("/delete-all")
    public String deleteAllTopics(RedirectAttributes redirectAttributes) {
        try {
            topicRepository.deleteAll();
            publicMessageRepository.deleteAll(); // מוחק גם הודעות ציבוריות
            redirectAttributes.addFlashAttribute("message", "All topics and messages have been deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting all topics: " + e.getMessage());
        }
        return "redirect:/admin/topics";
    }

    @GetMapping("/admin/topics/edit/{id}")
    public String showEditTopicForm(@PathVariable Long id, Model model) {
        Topic topic = topicService.getTopicById(id); // מחזיר את ה-Topic לפי ID
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found for the given ID: " + id);
        }
        model.addAttribute("topic", topic);
        return "edit-topic"; // מחזיר את שם דף ה-HTML לעריכה
    }

}
