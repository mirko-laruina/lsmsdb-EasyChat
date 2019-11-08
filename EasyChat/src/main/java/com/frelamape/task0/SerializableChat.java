package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

public class SerializableChat {
    public long chatId;
    public boolean isAdmin;
    public List<SerializableUser> members;
    public String name;
    public String lastActivity;

    public SerializableChat(long chatId, boolean isAdmin, List<SerializableUser> members, String name, String lastActivity) {
        this.chatId = chatId;
        this.isAdmin = isAdmin;
        this.members = members;
        this.name = name;
        this.lastActivity = lastActivity;
    }

    public SerializableChat(Chat chat){
        this.chatId = chat.getId();
        this.isAdmin = false;
        this.members = new ArrayList<>();
        for (User member:chat.getMembers()){
            members.add(new SerializableUser(member.getUserId(), member.getUsername(), null));
        }
        this.name = chat.getName();
        this.lastActivity = chat.getStringLastActivity();
    }
}