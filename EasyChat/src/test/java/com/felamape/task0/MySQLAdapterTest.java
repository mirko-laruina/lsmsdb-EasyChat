package com.felamape.task0;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MySQLAdapterTest {
    private DatabaseAdapter db;
    private final static long USERID = 101;
    private final static long ADD_USERID = 102;
    private final static long ADD_USERID2 = 103;
    private final static String MESSAGE = "Lorem ipsum dolor sit amet " + new Date().getTime();
    private final static long CHATID = 200;

    @Before
    public void setUp() throws Exception {
        db = new MySQLAdapter(new Settings("server.config").getConnStr());
    }

    @Test
    public void getChats() {
        List<Chat> chats = db.getChats(USERID);
        System.out.println(String.format("User %d has %d chats", USERID, chats.size()));
        for (Chat chat:chats){
            System.out.println(String.format("%d: %s", chat.getId(), chat.getName()));
        }
        assert chats.size() > 0;
    }

    @Test
    public void getChatMessages() {
        List<Message> messages = db.getChatMessages(CHATID, null, null, 10);
        System.out.println(String.format("Chat %d has %d messages", CHATID, messages.size()));
        for (Message message:messages){
            System.out.println(String.format("%d: %s %s", message.getMessageId(), message.getText(), message.getTimestamp().toString()));
        }

        assert messages.size() == 10;
    }

    @Test
    public void getChatMembers() {
        List<User> members = db.getChatMembers(CHATID);
        System.out.println(String.format("Chat %d has %d members", CHATID, members.size()));
        for (User member:members){
            System.out.println(member.getUsername());
        }

        assert members.size() > 0;
    }

    @Test
    public void addRemoveChatMember() {
        int oldChatMembers = db.getChatMembers(CHATID).size();
        assert db.addChatMember(CHATID, ADD_USERID);
        int newChatMembers = db.getChatMembers(CHATID).size();
        assertEquals(oldChatMembers + 1, newChatMembers);
        assert db.removeChatMember(CHATID, ADD_USERID);
        int newNewChatMembers = db.getChatMembers(CHATID).size();
        assertEquals(oldChatMembers, newNewChatMembers);
    }

    @Test
    public void addChatMessage() {
        int oldChatMessages = db.getChatMessages(CHATID, null, null, 0).size();
        assert db.addChatMessage(new Message(
                CHATID,
                new User(USERID),
                new Date(),
                MESSAGE
        )) != -1;
        try {   //TODO I should not need to sleep...
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int newChatMessages = db.getChatMessages(CHATID, null, null, 0).size();
        assertEquals(oldChatMessages + 1, newChatMessages);
        assertEquals(MESSAGE, db.getChatMessages(CHATID, null, null, 1).get(0).getText());
    }

    @Test
    public void createDeleteChat() {
        List<Long> users = new ArrayList<>();
        users.add(ADD_USERID);
        users.add(ADD_USERID2);
        long chatId = db.createChat("Chat", USERID, users);
        assert chatId != -1;
        List<User> members = db.getChatMembers(chatId);
        assert members.contains(new User(USERID));
        assert members.contains(new User(ADD_USERID));
        //assert db.deleteChat(chatId);
        //assert db.getChatMembers(chatId).isEmpty();
    }

    @Test
    public void createUserAndGetPassword() {
        String username = UUID.randomUUID().toString();
        long userId = db.createUser(new User(username, "password"));
        assert userId != -1;
        String password = db.getUserDBPassword(username);
        assert password.equals("password");
    }

}
