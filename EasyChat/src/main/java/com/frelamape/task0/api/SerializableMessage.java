package com.frelamape.task0.api;

import com.frelamape.task0.db.MessageEntity;

public class SerializableMessage {
    private long messageId;
    private String text;
    private String timestamp;
    private SerializableUser sender;

    public SerializableMessage(){}

    public SerializableMessage(MessageEntity message){
        this.messageId = message.getMessageId();
        this.text = message.getText();
        this.timestamp = message.getTimestamp().toString();
        this.sender = new SerializableUser(message.getSender(), false);
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public SerializableUser getSender() {
        return sender;
    }

    public void setSender(SerializableUser sender) {
        this.sender = sender;
    }
}