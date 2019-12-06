package com.frelamape.task0.db.leveldb;

import com.frelamape.task0.db.Message;
import com.frelamape.task0.db.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MessageSerializerTest {
    private static final Instant instant = Instant.ofEpochMilli(Instant.now().toEpochMilli());
    private static final Message m1 = new Message(0, new User(0), instant, "Lorem ipsum");
    private static final Message m2 = new Message(1, new User(1), instant, "Lorem ipsum èèèè+è+°çç°*");

    private static void assertMessagesEquals(Message expected, Message actual){
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertEquals(expected.getMessageId(), actual.getMessageId());
        assertEquals(expected.getSender(), actual.getSender());
    }

    public void testMessage(Message expMsg){
        Message actMsg = MessageSerializer.deserialize(expMsg.getMessageId(), MessageSerializer.serialize(expMsg));
        assertMessagesEquals(expMsg, actMsg);
    }

    @Test
    public void test(){
        testMessage(m1);
        testMessage(m2);
    }
}