package com.felamape.task0
public class User {
    private long userId;
    private String username;
    private String password;

    public User(long userId) {
        this.userId = userId;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public User(long userId, String username, String password){
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User)
            return ((User)obj).getUserId() == this.getUserId();
        else
            return super.equals(obj);
    }
}
