package com.frelamape.task0.db;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
@Ignore
public class DBAdapterTest {
    protected static DatabaseAdapter db;
    protected static long USERID;
    protected static long ADD_USERID;
    protected static long ADD_USERID2;
    protected static long ADD_USERID3;
    private final static String MESSAGE = "Lorem ipsum dolor sit amet " + new Date().getTime();
    protected static long CHATID;

    @Test
    public void getChats() {
        List<? extends ChatEntity> chats = db.getChats(USERID, true);
        System.out.println(String.format("User %d has %d chats", USERID, chats.size()));
        for (ChatEntity chat:chats){
            System.out.println(String.format("%d: %s", chat.getId(), chat.getName()));
        }
        assert chats.size() > 0;
    }

    @Test
    public void getChatMessages() {
        List<? extends MessageEntity> messages = db.getChatMessages(CHATID, -1, -1, 10);
        System.out.println(String.format("Chat %d has %d messages", CHATID, messages.size()));
        for (MessageEntity message:messages){
            System.out.println(String.format("%d: %s %s", message.getMessageId(), message.getText(), message.getTimestamp().toString()));
        }

        assert messages.size() > 0;
    }

    @Test
    public void getChatMembers() {
        List<? extends UserEntity> members = db.getChatMembers(CHATID);
        System.out.println(String.format("Chat %d has %d members", CHATID, members.size()));
        for (UserEntity member:members){
            System.out.println(member.getUsername());
        }

        assert members.size() > 0;
    }

    @Test
    public void addRemoveChatMember() {
        int oldChatMembers = db.getChatMembers(CHATID).size();
        assert db.addChatMember(CHATID, ADD_USERID);
        int newChatMembers = db.getChatMembers(CHATID).size();
        assert db.checkChatMember(CHATID, ADD_USERID);
        assert db.getChats(ADD_USERID, false).contains(new Chat(CHATID, "Chat"));
        assertEquals(oldChatMembers + 1, newChatMembers);
        assert db.removeChatMember(CHATID, ADD_USERID);
        assert !db.checkChatMember(CHATID, ADD_USERID);
        assert !db.getChats(ADD_USERID, false).contains(new Chat(CHATID, "Chat"));
        int newNewChatMembers = db.getChatMembers(CHATID).size();
        assertEquals(oldChatMembers, newNewChatMembers);
    }

    @Test
    public void addChatMessage() {
        int oldChatMessages = db.getChatMessages(CHATID, -1, -1, 0).size();
        assert db.addChatMessage(CHATID, new Message(
                new User(USERID),
                Instant.now(),
                MESSAGE
        )) != -1;
        int newChatMessages = db.getChatMessages(CHATID, -1, -1, 0).size();
        assertEquals(oldChatMessages + 1, newChatMessages);
        assertEquals(MESSAGE, db.getChatMessages(CHATID, -1, -1, 1).get(0).getText());
    }

    @Test
    public void createDeleteGroupChat() {
        List<Long> users = new ArrayList<>();
        users.add(ADD_USERID);
        users.add(ADD_USERID2);
        long chatId = db.createChat("Chat", USERID, users);
        assert chatId != -1;
        List<? extends UserEntity> members = db.getChatMembers(chatId);
        assert members.contains(new User(USERID));
        assert members.contains(new User(ADD_USERID));
        assert db.getChats(USERID, false).contains(new Chat(chatId, "Chat"));
        assert db.getChats(ADD_USERID, false).contains(new Chat(chatId, "Chat"));
        assert db.getChats(ADD_USERID2, false).contains(new Chat(chatId, "Chat"));
        assert db.deleteChat(chatId);
        assert db.getChatMembers(chatId) == null;
    }

    @Test
    public void createDeletePrivateChat() {
        List<Long> users = new ArrayList<>();
        users.add(ADD_USERID);
        long chatId = db.createChat("Chat", USERID, users);
        assert chatId != -1;
        List<? extends UserEntity> members = db.getChatMembers(chatId);
        assert members.contains(new User(USERID));
        assert members.contains(new User(ADD_USERID));
        assert db.existsChat(ADD_USERID, USERID);
        assert db.deleteChat(chatId);
        assert db.getChatMembers(chatId) == null;
    }

    @Test
    public void createUserAndGetPassword() {
        String username = UUID.randomUUID().toString();
        long userId = db.createUser(new User(username, "password"));
        assert userId != -1;
        String password = db.getUser(username).getPassword();
        assert password.equals("password");
        System.out.println(String.format("%d %s %s", userId, username, password));
    }

    @Test
    public void setGetRemoveSession() {
        UserSession session = new UserSession(USERID);
        assert db.setUserSession(session);
        assertEquals(USERID, db.getUserFromSession(session.getSessionId()));
        assert db.removeSession(session.getSessionId());
        assertEquals(-1, db.getUserFromSession(session.getSessionId()));
    }
}
