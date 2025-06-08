package com.example.forum.scheduler;

import com.example.forum.entities.PublicMessage;
import com.example.forum.repositories.PublicMessageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ForumScheduler {

    private final PublicMessageRepository publicMessageRepository;

    public ForumScheduler(PublicMessageRepository publicMessageRepository) {
        this.publicMessageRepository = publicMessageRepository;
    }

    /**
     * רץ פעם בשבוע (יום ראשון, 03:00)
     * cron = "0 0 3 ? * SUN"
     */
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void deleteOldPublicMessages() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(14);
        List<PublicMessage> oldMessages = publicMessageRepository.findByCreatedAtBeforeAndDeletedFalse(cutoff);
        if (!oldMessages.isEmpty()) {
            int batchSize = 100;
            for (int i = 0; i < oldMessages.size(); i += batchSize) {
                int end = Math.min(i + batchSize, oldMessages.size());
                List<PublicMessage> batch = oldMessages.subList(i, end);
                batch.forEach(pm -> pm.setDeleted(true));
                publicMessageRepository.saveAll(batch);
            }
            System.out.println("Scheduler: marked " + oldMessages.size() + " messages as deleted");
        }
    }
}
