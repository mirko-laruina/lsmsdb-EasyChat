package com.frelamape.task0;

public class SerializableMessage {
    public long messageId;
    public String text;
    public String timestamp;
    public SerializableUser sender;

    public SerializableMessage(long messageId, String text, String timestamp, SerializableUser sender) {
        this.messageId = messageId;
        this.text = text;
        this.timestamp = timestamp;
        this.sender = sender;
    }

    public SerializableMessage(Message message){
        this(message.getMessageId(), message.getText(), message.getStringTimestamp(),
                new SerializableUser(
                        message.getSender().getUserId(),
                        message.getSender().getUsername(),
                        null)
        );
    }
}