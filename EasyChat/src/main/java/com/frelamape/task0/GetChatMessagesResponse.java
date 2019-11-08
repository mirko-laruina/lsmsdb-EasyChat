package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

public class GetChatMessagesResponse extends BasicResponse {
    private List<Message> messages = new ArrayList<>();

    public GetChatMessagesResponse(List<Message> messages){
        super(true);
        addAll(messages);
    }

    public void add(Message message){
        messages.add(message);
    }

    public void addAll(List<Message> messages){
        for (Message message:messages){
            add(message);
        }
    }
}
