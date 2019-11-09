package com.frelamape.task0.api;

public class BasicResponse {
    private boolean success;

    public BasicResponse(boolean v){
        success = v;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
