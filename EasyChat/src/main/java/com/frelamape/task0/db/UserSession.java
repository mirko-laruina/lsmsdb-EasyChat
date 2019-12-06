package com.frelamape.task0.db;

import java.time.Instant;

public class UserSession extends UserSessionEntity {
    private String sessionId;
    private long userId;
    private transient Instant expiry;

    public UserSession(){
    }

    public UserSession(UserEntity user){
        this(user.getUserId());
    }

    public UserSession (long userId){
        this.userId = userId;
        setSessionId(SessionGenerator.generateSessionId());
    }

    public UserSession (long userId, String sessionId){
        this.userId = userId;
        this.sessionId = sessionId;
    }

    @Override
    public long getUserId(){
        return userId;
    }

    public void setUserId(long userId){
        this.userId = userId;
    }

    @Override
    public String getSessionId(){
        return sessionId;
    }

    public void setSessionId(String sid){
        this.sessionId = sid;
    }

    @Override
    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }
}