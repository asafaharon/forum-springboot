package com.example.forum.entities;

import com.example.forum.dto.TopicDTO;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "topics")
public class TopicsWrapper {
    private List<TopicDTO> topics;

    @XmlElement(name = "topic")
    public List<TopicDTO> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDTO> topics) {
        this.topics = topics;
    }
}
