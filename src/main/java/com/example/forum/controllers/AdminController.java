package com.example.forum.controllers;

import com.example.forum.entities.User;
import com.example.forum.repositories.PrivateMessageRepository;
import com.example.forum.repositories.PublicMessageRepository;
import com.example.forum.repositories.TopicRepository;
import com.example.forum.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final PrivateMessageRepository privateMessageRepository;
    private final PublicMessageRepository publicMessageRepository;

    public AdminController(UserRepository userRepository,
                           TopicRepository topicRepository,
                           PrivateMessageRepository privateMessageRepository,
                           PublicMessageRepository publicMessageRepository) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.privateMessageRepository = privateMessageRepository;
        this.publicMessageRepository = publicMessageRepository;
    }

    @GetMapping("/dashboard")
    public String getAdminDashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalTopics = topicRepository.count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalTopics", totalTopics);

        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    @PostMapping("/users/delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            try {
                // מחיקת הודעות פרטיות והודעות ציבוריות
                privateMessageRepository.deleteByReceiver(user);
                privateMessageRepository.deleteBySender(user);
                publicMessageRepository.deleteByAuthor(user);
                userRepository.delete(user);
                log.info("User with id {} deleted successfully", id);
            } catch (Exception e) {
                log.error("Error deleting user with id {}: {}", id, e.getMessage(), e);
                redirectAttributes.addFlashAttribute("error", "Error deleting user.");
                return "redirect:/admin/users";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found.");
        }
        return "redirect:/admin/users";
    }
}
