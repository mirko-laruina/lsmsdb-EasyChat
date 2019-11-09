package com.frelamape.task0.api;

import com.frelamape.task0.db.ChatEntity;
import com.frelamape.task0.db.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class SerializableChat {
    private long chatId;
    private boolean isAdmin;
    private List<SerializableUser> members;
    private String name;

    public SerializableChat(){}

    public SerializableChat(ChatEntity chat){
        this.chatId = chat.getId();
        this.isAdmin = false;
        this.members = new ArrayList<>();
        for (UserEntity member:chat.getMembers()){
            members.add(new SerializableUser(member, false));
        }
        this.name = chat.getName();
    }

    public SerializableChat(ChatEntity chat, long userId){
        this(chat);
        this.isAdmin = chat.getAdminId() == userId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<SerializableUser> getMembers() {
        return members;
    }

    public void setMembers(List<SerializableUser> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}