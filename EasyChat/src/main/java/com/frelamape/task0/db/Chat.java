package com.frelamape.task0.db;

import java.time.Instant;
import java.util.List;

public class Chat extends ChatEntity {
    private long chatId;
    private String name;
    private long adminId;
    private Instant lastActivityInstant;
    private List<? extends UserEntity> members;

    public Chat() {}

    public Chat(long chatId) {
        this.chatId = chatId;
    }

    public Chat(long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

    @Override
    public long getId() {
        return chatId;
    }

    public void setId(long chatId) {
        this.chatId = chatId;
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
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    @Override
    public Instant getLastActivityInstant() {
        return lastActivityInstant;
    }

    public void setLastActivityInstant(Instant lastActivityInstant) {
        this.lastActivityInstant = lastActivityInstant;
    }

    @Override
    public List<? extends UserEntity> getMembers() {
        return members;
    }

    public void setMembers(List<? extends UserEntity> chatMembers) {
        this.members = chatMembers;
    }
}
