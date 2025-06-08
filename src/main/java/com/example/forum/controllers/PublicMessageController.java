package com.example.forum.controllers;

import com.example.forum.entities.PublicMessage;
import com.example.forum.entities.Topic;
import com.example.forum.entities.User;
import com.example.forum.repositories.PublicMessageRepository;
import com.example.forum.repositories.TopicRepository;
import com.example.forum.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/public")
public class PublicMessageController {

    private static final Logger logger = LoggerFactory.getLogger(PublicMessageController.class);

    private final PublicMessageRepository publicMessageRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public PublicMessageController(PublicMessageRepository publicMessageRepository,
                                   TopicRepository topicRepository,
                                   UserRepository userRepository) {
        this.publicMessageRepository = publicMessageRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/topic/{topicId}/add")
    public String addPublicMessage(@PathVariable Long topicId,
                                   @RequestParam("content") String content,
                                   Authentication auth) {
        try {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

            User user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            PublicMessage pm = new PublicMessage();
            pm.setTopic(topic);
            pm.setAuthor(user);
            pm.setContent(content);
            pm.setDeleted(false);
            // אתחול תאריך יצירת ההודעה לערך הזמן הנוכחי
            pm.setCreatedAt(LocalDateTime.now());

            publicMessageRepository.save(pm);
            return "redirect:/topics/" + topicId;
        } catch (Exception e) {
            logger.error("Error adding public message for topic id " + topicId, e);
            return "redirect:/topics/" + topicId + "?error";
        }
    }

    @PostMapping("/{msgId}/delete")
    public String deletePublicMessage(@PathVariable Long msgId,
                                      @RequestParam Long topicId,
                                      Authentication auth) {
        try {
            PublicMessage pm = publicMessageRepository.findById(msgId)
                    .orElseThrow(() -> new IllegalArgumentException("Message not found"));

            // בדיקה אם המשתמש המחובר הוא מנהל
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // אם המשתמש הוא מנהל, או אם הוא המחבר של ההודעה – מאפשרים מחיקה
            if (isAdmin || (pm.getAuthor() != null && pm.getAuthor().getUsername().equals(auth.getName()))) {
                pm.setDeleted(true);
                publicMessageRepository.save(pm);
                return "redirect:/topics/" + topicId;
            } else {
                return "redirect:/topics/" + topicId + "?error=notAllowed";
            }
        } catch (Exception e) {
            // ניתן להוסיף לוג או טיפול בשגיאות
            return "redirect:/topics/" + topicId + "?error";
        }
    }

}
