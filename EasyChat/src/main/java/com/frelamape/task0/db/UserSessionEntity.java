package com.frelamape.task0.db;

import java.time.Instant;

public abstract class UserSessionEntity {
    public abstract long getUserId();

    public abstract String getSessionId();

    public abstract Instant getExpiry();
}