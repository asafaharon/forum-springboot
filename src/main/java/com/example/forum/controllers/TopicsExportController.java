package com.example.forum.controllers;

import com.example.forum.dto.TopicDTO;
import com.example.forum.entities.TopicsWrapper;
import com.example.forum.entities.Topic;
import com.example.forum.repositories.TopicRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class TopicsExportController {

    private final TopicRepository topicRepository;

    public TopicsExportController(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export-xml")
    public ResponseEntity<String> exportTopicsToXml() {
        try {
            // המרת נושאים ל-DTO כדי למנוע קשרים מחזוריים
            List<TopicDTO> topicDTOs = topicRepository.findAll().stream()
                    .map(topic -> new TopicDTO(topic.getId(), topic.getName(), topic.getDescription()))
                    .collect(Collectors.toList());

            if (topicDTOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No topics found.");
            }

            // עטיפת הנתונים ב-TopicsWrapper
            TopicsWrapper wrapper = new TopicsWrapper();
            wrapper.setTopics(topicDTOs);

            // המרת הרשימה ל-XML
            JAXBContext jaxbContext = JAXBContext.newInstance(TopicsWrapper.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(wrapper, writer);
            String xmlContent = writer.toString();

            // הכנת הקובץ להורדה
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=topics.xml");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/xml");

            return new ResponseEntity<>(xmlContent, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating XML: " + e.getMessage());
        }
    }
}
