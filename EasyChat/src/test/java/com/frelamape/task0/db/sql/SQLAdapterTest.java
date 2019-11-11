package com.frelamape.task0.db.sql;

import com.frelamape.task0.Settings;
import com.frelamape.task0.db.DBAdapterTest;
import com.frelamape.task0.db.jpa.JPAAdapter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@Ignore
public class SQLAdapterTest extends DBAdapterTest {

    @BeforeClass
    public static void setUp() throws Exception {
        db = new SQLAdapter(new Settings("server.config").getConnStr());
        USERID = 101;
        ADD_USERID = 102;
        ADD_USERID2 = 103;
        CHATID = 200;
    }

    @AfterClass
    public static void tearDown(){
        db.close();
    }

}
