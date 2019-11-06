package com.frelamape.task0;

public class LoginResponse extends BasicResponse {
    String sessionId;

    public LoginResponse(boolean r, String sessionId){
        super(r);
        this.sessionId = sessionId;
    }
}
