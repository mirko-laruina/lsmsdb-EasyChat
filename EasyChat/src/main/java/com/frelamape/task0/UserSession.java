package com.frelamape.task0;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "Sessions")
public class UserSession {
    @Id
    @GenericGenerator(name = "session_generator", strategy = "com.frelamape.task0.SessionGenerator")
    @GeneratedValue(generator = "session_generator")
    private String sessionId;

    @Column(name="userId")
    private long userId;

    @Column(name="expiry")
    private Timestamp expiry;

    @Transient
    private Instant expiryInstant;

    public UserSession(){
    }

    public UserSession  (User user){
        this.userId = user.getUserId();
    }

    public UserSession (long userId){
        this.userId = userId;
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

    public Timestamp getExpiry() {
        if (expiry == null && expiryInstant != null)
            expiry = Timestamp.from(expiryInstant);
        return expiry;
    }

    public void setExpiry(Timestamp expiry) {
        this.expiry = expiry;
    }

    public Instant getExpiryInstant() {
        if (expiryInstant == null && expiry != null)
            expiryInstant = expiry.toInstant();
        return expiryInstant;
    }

    public void setExpiryInstant(Instant expiryInstant) {
        this.expiryInstant = expiryInstant;
        this.setExpiry(Timestamp.from(this.expiryInstant));
    }
}