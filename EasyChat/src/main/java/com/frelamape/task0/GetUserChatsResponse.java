package com.frelamape.task0;

import java.util.ArrayList;
import java.util.List;

class UserChat {
   private long chatId;
   private boolean isAdmin;
   private List<UserChatMember> members;
   private String name;

   public long getChatId(){
       return this.chatId;
   }
   public void setChatId(long chatId) {
       this.chatId = chatId;
   }
   public boolean getIsAdmin(){
       return this.isAdmin;
   }
   public void setIsAdmin(boolean isAdmin) {
       this.isAdmin = isAdmin;
   }
   public List<UserChatMember> getMembers(){
       return this.members;
   }
   public void setMembers(List<UserChatMember> members) {
       this.members = members;
   }
   public String getName(){
       return this.name;
   }
   public void setName(String name){
       this.name = name;
   }
}

class UserChatMember {
    private long userId;
    private String username;

    public void setUserId(long userId) {
        this.userId = userId;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }

    public UserChatMember(long userId, String username){
        this.userId = userId;
        this.username = username;
    }
}

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
        List<UserChatMember> membersId = new ArrayList<>();
        for(User member: members){
            membersId.add(new UserChatMember(member.getUserId(), member.getUsername()));
        }
        uc.setMembers(membersId);
        chats.add(uc);
    }

    public List<UserChat> getChats(){
        return this.chats;
    }
}
