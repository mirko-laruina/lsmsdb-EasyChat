package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

public class GetUserChatsResponse extends BasicResponse{
    private List<Chat> chats = new ArrayList<>();
    private long userId;

    public GetUserChatsResponse(long userId){
        super(true);
        this.userId = userId;
    }

    public void add(Chat chat){
        chat.setAdmin(chat.getAdminId() == this.userId);
        chats.add(chat);
    }

    public void addAll(List<Chat> chats) {
        for (Chat chat:chats){
            add(chat);
        }
    }

    public List<Chat> getChats(){
        return this.chats;
    }
}
