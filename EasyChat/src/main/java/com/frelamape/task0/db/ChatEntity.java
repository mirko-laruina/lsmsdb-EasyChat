package com.frelamape.task0.db;

import java.time.Instant;
import java.util.List;

public abstract class ChatEntity implements Comparable<ChatEntity> {
    public abstract long getId();

    public abstract String getName();

    public abstract long getAdminId();

    public abstract Instant getLastActivityInstant();

    public abstract List<? extends UserEntity> getMembers();

    @Override
    public int compareTo(ChatEntity chat) {
        if(chat.getLastActivityInstant() == null){
            return -1;
        }
        if(this.getLastActivityInstant() == null){
            return 1;
        }
        return chat.getLastActivityInstant().compareTo(this.getLastActivityInstant());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChatEntity){
            return this.getId() == ((ChatEntity)obj).getId();
        } else {
            return false;
        }
    }
}
