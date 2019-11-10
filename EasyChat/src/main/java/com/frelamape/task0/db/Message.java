package com.frelamape.task0.db;

import java.time.Instant;

public class Message extends MessageEntity {
    private long messageId;
    private User sender;
    private Instant timestamp;
    private String text;

    public Message(){}

    public Message(User sender, Instant timestampInstant, String text) {
        this.sender = sender;
        this.timestamp = timestampInstant;
        this.text = text;
    }

    public Message(long id, User sender, Instant timestampInstant, String text) {
        this.messageId = id;
        this.sender = sender;
        this.timestamp = timestampInstant;
        this.text = text;
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public UserEntity getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
