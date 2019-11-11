package com.frelamape.task0.db.jpa;

import com.frelamape.task0.db.ChatEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Chats")
class Chat extends ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long chatId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "chat", cascade = { CascadeType.ALL } )
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "Chatmembers",
            joinColumns = @JoinColumn(name = "chatId"),
            inverseJoinColumns = @JoinColumn(name = "userId")
    )
    private List<User> members = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "adminId")
    private User admin;

    @Column(name = "lastActivity")
    private Timestamp lastActivity;

    public Chat() {
    }

    public Chat(int id, String name, List<Message> messages, List<User> members, User admin) {
        this.chatId = id;
        this.name = name;
        this.messages = messages;
        this.members = members;
        this.admin = admin;
    }

    public Chat(long id, String name) {
        this.chatId = id;
        this.name = name;
    }

    public Chat(long id) {
        this.chatId = id;
    }

    public Chat(long id, String name, User admin) {
        this.chatId = id;
        this.name = name;
        this.admin = admin;
    }

    @Override
    public long getId() {
        return chatId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getAdminId() {
        return admin.getUserId();
    }

    @Override
    public Instant getLastActivityInstant() {
        if (lastActivity != null)
            return lastActivity.toInstant();
        else
            return null;
    }

    @Override
    public List<User> getMembers() {
        return members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public String getStringLastActivity(){
        if(this.lastActivity == null)
            return "0";
        return this.lastActivity.toInstant().toString();
    }

    public void setLastActivity(Timestamp lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Timestamp getLastActivity(){
        return this.lastActivity;
    }
}
