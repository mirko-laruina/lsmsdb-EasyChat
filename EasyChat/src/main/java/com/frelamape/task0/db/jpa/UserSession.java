package com.frelamape.task0.db.jpa;

import com.frelamape.task0.db.UserSessionEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "Sessions")
class UserSession extends UserSessionEntity {
    @Id
    private String sessionId;

    @Column(name="userId")
    private long userId;

    @Column(name="expiry")
    private Timestamp expiryTimestamp;

    @Transient
    private Instant expiry;

    public UserSession(){
    }

    public UserSession (User user){
        this(user.getUserId());
    }

    public UserSession (long userId){
        this.userId = userId;
    }

    public UserSession(long userId, String sessionId) {
        this.sessionId = sessionId;
        this.userId = userId;
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

    public Timestamp getExpiryTimestamp() {
        if (expiryTimestamp == null && expiry != null)
            return Timestamp.from(expiry);
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(Timestamp expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    @Override
    public Instant getExpiry() {
        if (expiry == null && expiryTimestamp != null)
            return expiryTimestamp.toInstant();
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
        this.expiryTimestamp = Timestamp.from(this.expiry);
    }
}