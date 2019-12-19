package com.frelamape.task0.db;

import java.util.List;

public interface DatabaseAdapter {
    /**
     * Returns the list of chats for the user identified by the given userId.
     *
     * @return the list of chats or null in case of error.
     */
    List<? extends ChatEntity> getChats(long userId, boolean loadMembers);

    /**
     * Returns a list of messages for the given chat, in the given time range, up to the given
     * number of elements, sorted in ascending message sending time.
     *
     * @param chatId id of the chat whose messages are to be retrieved
     * @param from start of the time range (included). It can be {@code -1}, whose meaning is that there is no lower bound.
     * @param to end of the time range (excluded). It can be {@code -1}, whose meaning is that there is no upper bound.
     * @param n maximum number of elements to return. If from is not {@code -1}, messages are counted from {@code from} up
     *          to n or {@code to}. If from is {@code -1}, messages are counted from {@code to} up to n or {@code from}.
     * @return the list of messages or null in case of error.
     */
    List<? extends MessageEntity> getChatMessages(long chatId, long from, long to, int n);

    /**
     * Returns the list of members for the chat identified by the given chatId.
     *
     * @return the list of members or null in case of error.
     */
    List<? extends UserEntity> getChatMembers(long chatId);

    /**
     * Adds the user identified by the given userId to the chat identified by the given chatId.
     *
     * @return {@code true} in case of success, {@code false} otherwise.
     */
    boolean addChatMember(long chatId, long userId);

    /**
     * Removes the user identified by the given userId from the chat identified by the given chatId.
     *
     * @return {@code true} in case of success, {@code false} otherwise.
     */
    boolean removeChatMember(long chatId, long userId);

    /**
     * Checks whether the user identified by the given userId is inside the chat identified by the given chatId.
     *
     * @return {@code true} if the user is a member of the cat, {@code false} otherwise or in case of error.
     */
    boolean checkChatMember(long chatId, long userId);

    /**
     * Adds the given message to the appropriate chat.
     *
     * @return the id of the added message.
     */
    long addChatMessage(long chatId, MessageEntity message);

    /**
     * Creates a new chat with the given name, admin and members.
     *
     * The admin is always added as member, even if not present in the provided member list.
     *
     * @return the id of the created chat or -1 in case of error.
     */
    long createChat(String name, long adminId, List<Long> userIds);

    /**
     * Deletes the chat with the given chatId.
     *
     * @return {@code true} in case of success, {@code false} otherwise.
     */
    boolean deleteChat(long chatId);

    /**
     * Returns the chat identified by the given chatId.
     *
     * @return the chat or null in case of error.
     */
    ChatEntity getChat(long chatId, boolean loadMembers);

    /**
     * Registers a new user.
     *
     * @return the id of the created chat or -1 in case of error.
     */
    long createUser(UserEntity user);

    /**
     * Returns user identified by the given username.
     *
     * @return the user in case of success, null otherwise.
     */
    UserEntity getUser(String username);

    /**
     * Returns user identified by the given userId.
     *
     * @return the user in case of success, null otherwise.
     */
    UserEntity getUser(long userId);

    /**
     * Returns userId of the user who owns the session identified by the given sessionId.
     *
     * In case the session is expired, the session is deleted from the db and the result is the same
     * as if no session were present.
     *
     * @return the id of the user in case of success, -1 otherwise.
     */
    long getUserFromSession(String sessionId);

    /**
     * Creates a new session.
     *
     * @return {@code true} in case of success, {@code false} otherwise.
     */
    boolean setUserSession(UserSessionEntity user);

    /**
     * Removes the session identified by the given sessionId.
     *
     * @return {@code true} in case of success, {@code false} otherwise.
     */
    boolean removeSession(String sessionId);

    /**
     * Returns true if there exists a private chat between user1 and user2.
     *
     * NB: a private chat is a chat with only two members.
     *
     * @return {@code true} if it exists, {@code false} otherwise or in case of errors.
     */
    boolean existsChat(long user1, long user2);

    void close();
}
