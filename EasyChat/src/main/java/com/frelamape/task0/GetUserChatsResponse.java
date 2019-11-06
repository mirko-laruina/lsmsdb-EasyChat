package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

public class GetUserChatsResponse {
    private List<UserChat> chats = new ArrayList<>();
    private long userId;

    public GetUserChatsResponse(long userId){
        this.userId = userId;
    }

    public void add(Chat chat){
        UserChat uc = new UserChat();
        uc.setIsAdmin(false);
        uc.setChatId(chat.getId());
        uc.setName(chat.getName());
        if(chat.getAdmin().getUserId() == this.userId){
            uc.setIsAdmin(true);
        }

        List<User> members = chat.getMembers();
        List<UserInfo> membersId = new ArrayList<>();
        for(User member: members){
            membersId.add(new UserInfo(member.getUserId(), member.getUsername()));
        }
        uc.setMembers(membersId);
        chats.add(uc);
    }

    public List<UserChat> getChats(){
        return this.chats;
    }
}
