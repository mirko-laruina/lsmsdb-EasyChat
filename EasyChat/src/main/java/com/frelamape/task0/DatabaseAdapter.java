package com.frelamape.task0;

import java.util.Date;
import java.util.List;

public interface DatabaseAdapter {
    List<Chat> getChats(long userId);
    List<Message> getChatMessages(long chatId, Date from, Date to, int n);
    List<User> getChatMembers(long chatId);
    boolean addChatMember(long chatId, long userId);
    boolean removeChatMember(long chatId, long userId);
    boolean checkChatMember(long chatId, long userId);
    long addChatMessage(Message message);
    long createChat(String name, long adminId, List<Long> userIds);
    boolean deleteChat(long chatId);
    Chat getChat(long chatId);
    long createUser(User user);
    String getUserDBPassword(String username);
    long getUserId(String username);
    long getUserFromSession(String sessionId);
    boolean setUserSession(long userId, String sessionId);
    boolean removeSession(String sessionId);

    void close();
}