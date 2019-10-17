package com.frelamape.task0;

import java.util.List;

public class Chat {
    private long chatId;
    private String name;
    private List<Message> messages;
    private List<User> members;
    private long adminId;
    //just a quick solution, this has to be reviewed
    public boolean isAdmin;

    public Chat(int id, String name, List<Message> messages, List<User> members, long adminId) {
        this.chatId = id;
        this.name = name;
        this.messages = messages;
        this.members = members;
        this.adminId = adminId;
    }

    public Chat(long id, String name) {
        this.chatId = id;
        this.name = name;
    }

    public Chat(long id, String name, long adminId) {
        this.chatId = id;
        this.name = name;
        this.adminId = adminId;
    }

    public long getId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public long getAdmin() {
        return adminId;
    }

    public void setAdmin(long adminId) {
        this.adminId = adminId;
    }
}
