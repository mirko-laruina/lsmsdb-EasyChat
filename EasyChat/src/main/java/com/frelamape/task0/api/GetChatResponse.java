package com.frelamape.task0.api;

import com.frelamape.task0.db.ChatEntity;

public class GetChatResponse extends BasicResponse {
    private SerializableChat chat;

    public GetChatResponse(ChatEntity chat){
        super(true);
        this.chat = new SerializableChat(chat);
    }
}
