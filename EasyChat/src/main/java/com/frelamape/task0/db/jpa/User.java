package com.frelamape.task0.db.jpa;

import com.frelamape.task0.db.UserEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Users")
class User extends UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @ManyToMany(mappedBy = "members")
    private List<Chat> chats;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    public User() {
    }

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

    public User(UserEntity user){
        this(user.getUserId(), user.getUsername(), user.getPassword());
    }

    @Override
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User)
            return ((User)obj).getUserId() == this.getUserId();
        else
            return super.equals(obj);
    }
}
