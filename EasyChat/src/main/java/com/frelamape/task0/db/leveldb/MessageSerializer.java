package com.frelamape.task0.db.leveldb;

import com.frelamape.task0.db.Message;
import com.frelamape.task0.db.MessageEntity;
import com.frelamape.task0.db.User;

import java.io.*;
import java.time.Instant;

public class MessageSerializer {
    public static Message deserialize(long msgId, byte[] bytes){
        ByteArrayInputStream bis = null;
        DataInputStream dis = null;
        try{
            bis = new ByteArrayInputStream(bytes);
            dis = new DataInputStream(bis);
            long userId = dis.readLong();
            Instant instant = Instant.ofEpochMilli(dis.readLong());
            String text = dis.readUTF();
            return new Message(msgId, new User(userId), instant, text);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        } finally {
            if (dis != null){
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] serialize(MessageEntity message) {
        ByteArrayOutputStream bos = null;
        DataOutputStream dos = null;
        try{
            bos = new ByteArrayOutputStream();
            dos = new DataOutputStream(bos);
            dos.writeLong(message.getSender().getUserId());
            dos.writeLong(message.getTimestamp().toEpochMilli());
            dos.writeUTF(message.getText());
            dos.flush();
            return bos.toByteArray();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        } finally {
            if (dos != null){
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
