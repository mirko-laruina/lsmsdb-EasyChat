package com.frelamape.task0.api;

import com.frelamape.task0.db.UserEntity;

public class SerializableUser {
    private long userId;
    private String username;
    private String password;

    public SerializableUser(){}

    public SerializableUser(UserEntity user, boolean includePassword){
        this.userId = user.getUserId();
        this.username = user.getUsername();
        if (includePassword)
            this.password = user.getPassword();
    }

    public SerializableUser(UserEntity user){
        this(user, true);
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
}
