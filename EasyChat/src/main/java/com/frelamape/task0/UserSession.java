package com.frelamape.task0;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Entity
@Table(name = "Sessions")
public class UserSession {
    @Id
    private String sessionId;

    @Column(name="userId")
    private long userId;

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public UserSession(){
    }

    public UserSession  (User user){
        this.sessionId = generateSessionId();
        this.userId = user.getUserId();
    }

    public UserSession (long userId){
        this.sessionId = generateSessionId();
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

    private String generateSessionId(){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[64];
        random.nextBytes(bytes);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] msgDigest = digest.digest(bytes);
            return bytesToHex(msgDigest);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    //from https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    //expiry is only used at db level. No need here
}