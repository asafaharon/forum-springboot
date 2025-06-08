package com.example.forum.controllers;

import com.example.forum.entities.PrivateMessage;
import com.example.forum.entities.User;
import com.example.forum.repositories.PrivateMessageRepository;
import com.example.forum.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/private")
public class PrivateMessageController {

    private final PrivateMessageRepository privateMessageRepository;
    private final UserRepository userRepository;

    public PrivateMessageController(PrivateMessageRepository privateMessageRepository,
                                    UserRepository userRepository) {
        this.privateMessageRepository = privateMessageRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/inbox")
    public String inbox(Authentication auth, Model model) {
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<PrivateMessage> inbox = privateMessageRepository
                .findByReceiverAndDeletedByReceiverFalseOrderByCreatedAtDesc(currentUser);
        model.addAttribute("inbox", inbox);
        return "private-inbox";
    }

    @GetMapping("/outbox")
    public String outbox(Authentication auth, Model model,
                         @RequestParam(required = false) String error) {
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<PrivateMessage> outbox = privateMessageRepository
                .findBySenderAndDeletedBySenderFalseOrderByCreatedAtDesc(currentUser);
        model.addAttribute("outbox", outbox);

        Long countNotDeleted = privateMessageRepository.countBySenderAndDeletedBySenderFalse(currentUser);
        model.addAttribute("canSendMore", countNotDeleted < 100);
        model.addAttribute("error", error);

        return "private-outbox";
    }

    @GetMapping("/send")
    public String showSendForm(Model model) {
        model.addAttribute("allUsers", userRepository.findAll());
        return "private-send";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam Long receiverId,
                              @RequestParam String content,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        User sender = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // בדיקה - לא ניתן לשלוח הודעה לעצמך
        if (sender.getId().equals(receiverId)) {
            redirectAttributes.addFlashAttribute("error", "Cannot send a message to yourself.");
            return "redirect:/private/send";
        }

        // בדיקת אורך ותוכן ההודעה
        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Message content cannot be empty.");
            return "redirect:/private/send";
        }
        if (content.length() > 500) {
            redirectAttributes.addFlashAttribute("error", "Message is too long (max 500 characters).");
            return "redirect:/private/send";
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Long countNotDeleted = privateMessageRepository.countBySenderAndDeletedBySenderFalse(sender);
        if (countNotDeleted >= 100) {
            redirectAttributes.addFlashAttribute("error", "You have reached the sending limit.");
            return "redirect:/private/outbox";
        }

        PrivateMessage pm = new PrivateMessage();
        pm.setSender(sender);
        pm.setReceiver(receiver);
        pm.setContent(content);

        privateMessageRepository.save(pm);

        return "redirect:/private/outbox";
    }

    @PostMapping("/{id}/delete")
    public String deletePrivateMessage(@PathVariable Long id,
                                       @RequestParam boolean isSender) {
        PrivateMessage pm = privateMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (isSender) {
            pm.setDeletedBySender(true);
            if (pm.isDeletedByReceiver()) {
                privateMessageRepository.delete(pm);
            } else {
                privateMessageRepository.save(pm);
            }
            return "redirect:/private/outbox";
        } else {
            pm.setDeletedByReceiver(true);
            if (pm.isDeletedBySender()) {
                privateMessageRepository.delete(pm);
            } else {
                privateMessageRepository.save(pm);
            }
            return "redirect:/private/inbox";
        }
    }
}
