package com.frelamape.task0;

import java.io.Serializable;
import java.time.Instant;

public class Message implements Comparable<Message>, Serializable {
    private long messageId;
    private User sender;
    private transient Instant timestampInstant;
    private String timestamp;
    private String text;

    public Message() {
    }

    public Message(User sender, Instant timestampInstant, String text) {
        this.sender = sender;
        setTimestampInstant(timestampInstant);
        this.text = text;
    }

    public Message(long id, User sender, Instant timestampInstant, String text) {
        this.messageId = id;
        this.sender = sender;
        setTimestampInstant(timestampInstant);
        this.text = text;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public Instant getTimestampInstant() {
        return timestampInstant;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestampInstant(Instant timestampInstant) {
        this.timestampInstant = timestampInstant;
        this.timestamp = timestampInstant.toString();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Override
    public int compareTo(Message message) {
        return this.getTimestamp().compareTo(message.getTimestamp());
    }
}
