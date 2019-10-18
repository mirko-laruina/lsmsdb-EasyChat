package com.frelamape.task0;

import java.time.Instant;

public class Message {
    private long messageId;
    private long chatId;
    private User sender;
    private transient Instant timestampInstant;
    private String timestamp;
    private String text;

    public Message(long chatId, User sender, Instant timestamp, String text) {
        this.chatId = chatId;
        this.sender = sender;
        this.timestampInstant = timestamp;
        this.text = text;
        this.timestamp = timestamp.toString();
    }

    public Message(long id, long chatId, User sender, Instant timestamp, String text) {
        this.messageId = id;
        this.chatId = chatId;
        this.sender = sender;
        this.timestampInstant = timestamp;
        this.text = text;
        this.timestamp = timestamp.toString();
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Instant getTimestampInstant() {
        return timestampInstant;
    }

    public void setTimestampInstant(Instant timestampInstant) {
        this.timestampInstant = timestampInstant;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
}
