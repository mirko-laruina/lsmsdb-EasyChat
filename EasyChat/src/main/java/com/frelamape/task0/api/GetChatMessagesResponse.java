package com.frelamape.task0.api;

import com.frelamape.task0.db.MessageEntity;

import java.util.ArrayList;
import java.util.List;

public class GetChatMessagesResponse extends BasicResponse {
    private List<SerializableMessage> messages = new ArrayList<>();

    public GetChatMessagesResponse(List<? extends MessageEntity> messages){
        super(true);
        addAll(messages);
    }

    public void add(MessageEntity message){
        messages.add(new SerializableMessage(message));
    }

    public void addAll(List<? extends MessageEntity> messages){
        for (MessageEntity message:messages){
            add(message);
        }
    }
}
