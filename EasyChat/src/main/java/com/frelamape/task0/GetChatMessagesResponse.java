package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

public class GetChatMessagesResponse extends BasicResponse {
    private List<SerializableMessage> messages = new ArrayList<>();

    public GetChatMessagesResponse(List<Message> messages){
        super(true);
        addAll(messages);
    }

    public void add(Message message){
        messages.add(new SerializableMessage(message));
    }

    public void addAll(List<Message> messages){
        for (Message message:messages){
            add(message);
        }
    }
}
