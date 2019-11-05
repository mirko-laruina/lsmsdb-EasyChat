package com.frelamape.task0;

public class UserInfo {
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

    public UserInfo(long userId, String username){
        this.userId = userId;
        this.username = username;
    }
}
