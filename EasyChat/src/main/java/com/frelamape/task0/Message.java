package com.frelamape.task0;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "Messages")
public class Message implements Comparable<Message> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "chatId")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "senderUserId")
    private User sender;

    @Column(name = "timestamp")
    private Timestamp sqlTimestamp;

    @Transient
    private Instant instantTimestamp;

    @Column(name = "text")
    private String text;

    public Message() {
    }

    public Message(Chat chat, User sender, Instant stringTimestamp, String text) {
        this.chat = chat;
        this.sender = sender;
        setInstantTimestamp(stringTimestamp);
        this.text = text;
    }

    public Message(long id, Chat chat, User sender, Instant stringTimestamp, String text) {
        this.messageId = id;
        this.chat = chat;
        this.sender = sender;
        setInstantTimestamp(stringTimestamp);
        this.text = text;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Instant getInstantTimestamp() {
        if (instantTimestamp == null && sqlTimestamp != null)
            instantTimestamp = sqlTimestamp.toInstant();
        return instantTimestamp;
    }

    public void setInstantTimestamp(Instant instantTimestamp) {
        this.instantTimestamp = instantTimestamp;
    }

    public Timestamp getSqlTimestamp() {
        if (sqlTimestamp == null && instantTimestamp != null)
            sqlTimestamp = Timestamp.from(instantTimestamp);
        return sqlTimestamp;
    }

    public void setSqlTimestamp(Timestamp sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }

    public String getStringTimestamp() {
        return getInstantTimestamp().toString();
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
        return this.getInstantTimestamp().compareTo(message.getInstantTimestamp());
    }
}
