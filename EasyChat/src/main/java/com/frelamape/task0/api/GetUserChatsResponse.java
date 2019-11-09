package com.frelamape.task0.api;

import com.frelamape.task0.db.ChatEntity;

import java.util.ArrayList;
import java.util.List;

public class GetUserChatsResponse extends BasicResponse{
    private List<SerializableChat> chats = new ArrayList<>();
    private long userId;

    public GetUserChatsResponse(long userId){
        super(true);
        this.userId = userId;
    }

    public GetUserChatsResponse(long userId, List<? extends ChatEntity> chats){
        this(userId);
        addAll(chats);
    }

    public void add(ChatEntity chat){
        chats.add(new SerializableChat(chat, this.userId));
    }

    public void addAll(List<? extends ChatEntity> chats) {
        for (ChatEntity chat:chats){
            add(chat);
        }
    }

    public List<SerializableChat> getChats(){
        return this.chats;
    }
}
