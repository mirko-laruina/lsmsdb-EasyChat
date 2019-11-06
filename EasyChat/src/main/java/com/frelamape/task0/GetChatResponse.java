package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

public class GetChatResponse extends BasicResponse {
    private SerializableChat chat;

    public GetChatResponse(SerializableChat chat){
        super(true);
        this.chat = chat;
    }

    public GetChatResponse(Chat chat){
        this(new SerializableChat(chat));
    }
}
