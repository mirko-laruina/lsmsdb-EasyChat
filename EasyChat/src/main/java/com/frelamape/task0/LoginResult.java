package com.frelamape.task0;

public class LoginResult extends BooleanResult {
    String sessionId;

    public LoginResult(boolean r, String sessionId){
        super(r);
        this.sessionId = sessionId;
    }
}
