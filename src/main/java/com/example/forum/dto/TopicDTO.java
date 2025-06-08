package com.example.forum.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "topic")
public class TopicDTO {

    private Long id;
    private String name;
    private String description;

    public TopicDTO() {}

    public TopicDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @XmlElement
    public Long getId() {
        return id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }
}
