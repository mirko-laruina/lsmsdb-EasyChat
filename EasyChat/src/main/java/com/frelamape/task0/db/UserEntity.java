package com.frelamape.task0.db;

public abstract class UserEntity {

    public abstract long getUserId();

    public abstract String getUsername();

    public abstract String getPassword();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserEntity)
            return ((UserEntity)obj).getUserId() == this.getUserId();
        else
            return super.equals(obj);
    }
}
