package com.frelamape.task0.db.leveldb;

import com.frelamape.task0.db.DBAdapterTest;
import com.frelamape.task0.db.Message;
import com.frelamape.task0.db.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(JUnit4.class)
public class LevelDBAdapterTest extends DBAdapterTest {
    private final static String FILENAME = "/tmp/levelDBStore";

    @BeforeClass
    public static void setUp() throws Exception {
        db = new LevelDBAdapter(FILENAME);
        USERID = db.createUser(new User("carmela.dach", "omnis"));
        ADD_USERID = db.createUser(new User("darren31", "boh"));
        ADD_USERID2 = db.createUser(new User("stone53", "boh"));
        ADD_USERID3 = db.createUser(new User("graham.naomi", "boh"));
        CHATID = db.createChat("Chat", USERID, new ArrayList<>(Arrays.asList(ADD_USERID3, ADD_USERID2)));
        db.createChat("Chat", 0L, new ArrayList<>(Arrays.asList(USERID, ADD_USERID2)));
        db.addChatMessage(CHATID, new Message(new User(USERID), Instant.now(), "ciao"));
        db.addChatMessage(CHATID, new Message(new User(ADD_USERID), Instant.now(), "ciao"));
        db.addChatMessage(CHATID, new Message(new User(ADD_USERID2), Instant.now(), "ciao"));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        db.close();
        new File(FILENAME).delete();
    }
}
