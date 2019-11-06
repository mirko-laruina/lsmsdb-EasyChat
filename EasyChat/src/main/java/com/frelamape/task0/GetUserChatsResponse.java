package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

public class GetUserChatsResponse extends BasicResponse{
    private List<SerializableChat> chats = new ArrayList<>();
    private long userId;

    public GetUserChatsResponse(long userId){
        super(true);
        this.userId = userId;
    }

    public void add(Chat chat){
        SerializableChat uc = new SerializableChat(chat);
        uc.isAdmin = chat.getAdmin().getUserId() == this.userId;
        chats.add(uc);
    }

    public void addAll(List<Chat> chats) {
        for (Chat chat:chats){
            add(chat);
        }
    }

    public List<SerializableChat> getChats(){
        return this.chats;
    }


}
