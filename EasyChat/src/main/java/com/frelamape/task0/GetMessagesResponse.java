package com.frelamape.task0;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

class MessageResponse {
    private long messageId;
    private long chatId;
    private UserInfo sender;
    private String timestamp;
    private String text;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

public class GetMessagesResponse {
    List<MessageResponse> messages = new ArrayList<>();

    public GetMessagesResponse(List<Message> messages){
        for(Message message: messages){
            add(message);
        }
    }

    public void add(Message message){
        MessageResponse mr = new MessageResponse();
        mr.setMessageId(message.getMessageId());
        mr.setChatId(message.getChatId().getId());
        mr.setTimestamp(message.getStringTimestamp());
        mr.setText(message.getText());
        UserInfo ui = new UserInfo(message.getSender().getUserId(), message.getSender().getUsername());
        mr.setSender(ui);
        Gson gson = new Gson();
        System.out.println(gson.toJson(mr));
        this.messages.add(mr);
    }

    public List<MessageResponse> getMessages(){
        return this.messages;
    }
}
