package com.frelamape.task0.db.jpa;

import com.frelamape.task0.db.MessageEntity;
import com.frelamape.task0.db.UserEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "Messages")
class Message extends MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "chatId")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "senderUserId")
    private User userSender;

    @Column(name = "timestamp")
    private Timestamp sqlTimestamp;

    @Transient
    private Instant timestamp;

    @Column(name = "text")
    private String text;

    public Message() {
    }

    public Message(Chat chat, User userSender, Instant stringTimestamp, String text) {
        this.chat = chat;
        this.userSender = userSender;
        setTimestamp(stringTimestamp);
        this.text = text;
    }

    public Message(long id, Chat chat, User userSender, Instant stringTimestamp, String text) {
        this.messageId = id;
        this.chat = chat;
        this.userSender = userSender;
        setTimestamp(stringTimestamp);
        this.text = text;
    }

    public Message(long chatId, MessageEntity message) {
        this(
                message.getMessageId(),
                new Chat(chatId),
                new User(message.getSender()),
                message.getTimestamp(),
                message.getText()
            );
    }

    @Override
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

    @Override
    public Instant getTimestamp() {
        if (timestamp == null && sqlTimestamp != null)
            return sqlTimestamp.toInstant();
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getSqlTimestamp() {
        if (sqlTimestamp == null && timestamp != null)
            return Timestamp.from(timestamp);
        return sqlTimestamp;
    }

    public void setSqlTimestamp(Timestamp sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
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
        return userSender;
    }

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = new User(userSender);
    }
}
