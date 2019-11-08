package com.frelamape.task0;

import java.time.Instant;
import java.util.List;

public class Chat implements Comparable<Chat> {
    private long chatId;
    private String name;
    private long adminId;
    private transient Instant lastActivityInstant;
    private boolean isAdmin;
    private List<User> members;

    public Chat() {
    }

    public Chat(long id, String name) {
        this.chatId = id;
        this.name = name;
    }

    public Chat(long id) {
        this.chatId = id;
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

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public Instant getLastActivityInstant() {
        return lastActivityInstant;
    }

    public void setLastActivityInstant(Instant lastActivityInstant) {
        this.lastActivityInstant = lastActivityInstant;
    }

    public String getLastActivity(){
        return lastActivityInstant.toString();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    @Override
    public int compareTo(Chat chat) {
        if(chat.getLastActivityInstant() == null){
            return -1;
        }
        if(this.getLastActivityInstant() == null){
            return 1;
        }
        return chat.getLastActivityInstant().compareTo(this.getLastActivityInstant());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Chat){
            return this.getId() == ((Chat)obj).getId();
        } else {
            return false;
        }
    }
}
