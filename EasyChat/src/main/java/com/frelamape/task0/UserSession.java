package com.frelamape.task0;

import java.io.Serializable;
import java.time.Instant;

public class UserSession implements Serializable {
    private String sessionId;
    private long userId;
    private transient Instant expiry;
//    private String expiry;

    public UserSession(){
    }

    public UserSession  (User user){
        this(user.getUserId());
    }

    public UserSession (long userId){
        this.userId = userId;
        this.sessionId = SessionGenerator.generateSessionId();
    }

    public long getUserId(){
        return userId;
    }

    public void setUserId(long userId){
        this.userId = userId;
    }

    public String getSessionId(){
        return sessionId;
    }

    public void setSessionId(String sid){
        this.sessionId = sid;
    }

    public Instant getExpiryInstant() {
        return expiry;
    }

    public void setExpiryInstant(Instant expiry) {
        this.expiry = expiry;
    }

    public String getExpiry() {
        return expiry.toString();
    }
}