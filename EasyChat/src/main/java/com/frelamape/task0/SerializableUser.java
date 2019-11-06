package com.frelamape.task0;

public class SerializableUser {
    public long userId;
    public String username;
    public String password;

    public SerializableUser(long userId, String username, String password){
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public SerializableUser(User user){
        this(user.getUserId(), user.getUsername(), user.getPassword());
    }
}
