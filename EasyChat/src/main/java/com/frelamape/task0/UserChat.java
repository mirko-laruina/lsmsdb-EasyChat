package com.frelamape.task0;

import java.util.List;

public class UserChat {
   private long chatId;
   private boolean isAdmin;
   private List<UserInfo> members;
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
   public List<UserInfo> getMembers(){
       return this.members;
   }
   public void setMembers(List<UserInfo> members) {
       this.members = members;
   }
   public String getName(){
       return this.name;
   }
   public void setName(String name){
       this.name = name;
   }
}
