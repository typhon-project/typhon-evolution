package com.typhon.evolutiontool;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message")
public class Message {
    private String message;
    @Id
    private long id;

    public Message(String message, long id) {
        this.message = message;
        this.id = id;
    }

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public long getId() {
        return id;
    }
}