package com.frelamape.task0.api;

public class LoginResponse extends BasicResponse {
    private String sessionId;

    public LoginResponse(boolean r, String sessionId){
        super(r);
        this.sessionId = sessionId;
    }
}
