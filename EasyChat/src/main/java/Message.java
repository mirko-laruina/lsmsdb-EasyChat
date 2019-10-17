package com.felamape.task0;

import java.util.Date;

public class Message {
    private long messageId;
    private long chatId;
    private User sender;
    private Date timestamp;
    private String text;

    public Message(long chatId, User sender, Date timestamp, String text) {
        this.chatId = chatId;
        this.sender = sender;
        this.timestamp = timestamp;
        this.text = text;
    }

    public Message(long id, long chatId, User sender, Date timestamp, String text) {
        this.messageId = id;
        this.chatId = chatId;
        this.sender = sender;
        this.timestamp = timestamp;
        this.text = text;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
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
