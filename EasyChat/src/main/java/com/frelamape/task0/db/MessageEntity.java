package com.frelamape.task0.db;

import java.time.Instant;

public abstract class MessageEntity implements Comparable<MessageEntity> {
    public abstract long getMessageId();

    public abstract Instant getTimestamp();

    public abstract String getText();

    public abstract UserEntity getSender();

    @Override
    public int compareTo(MessageEntity message) {
        return this.getTimestamp().compareTo(message.getTimestamp());
    }
}
