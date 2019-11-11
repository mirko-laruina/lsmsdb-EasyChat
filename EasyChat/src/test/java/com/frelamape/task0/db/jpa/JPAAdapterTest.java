package com.frelamape.task0.db.jpa;

import com.frelamape.task0.db.DBAdapterTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@Ignore
public class JPAAdapterTest extends DBAdapterTest {

    @BeforeClass
    public static void setUp() throws Exception {
        db = new JPAAdapter();
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
