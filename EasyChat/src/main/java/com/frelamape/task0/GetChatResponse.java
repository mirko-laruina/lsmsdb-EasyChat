package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

public class GetChatResponse extends BasicResponse {
    private Chat chat;

    public GetChatResponse(Chat chat){
        super(true);
        this.chat = chat;
    }
}
