package com.frelamape.task0;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.fusesource.leveldbjni.JniDBFactory.*;

public class LevelDBAdapter implements DatabaseAdapter {
    private DB levelDBStore;

    public LevelDBAdapter(String filename) throws IOException {
        Options options = new Options();
        levelDBStore = factory.open(new File(filename), options);
    }

    @Override
    public List<Chat> getChats(long userId) {
        try {
            List<Chat> chats = new ArrayList<>();
            String chatListString = asString(levelDBStore.get(bytes(String.format("user:%d:chats", userId))));
            if (chatListString == null)
                return null;

            String[] chatStrings = chatListString.split(",");
            for (String chatString : chatStrings) {
                if (chatString.equals(""))
                    continue;
                Chat chat = getChat(Long.parseLong(chatString));
                chats.add(chat);
            }
            Collections.sort(chats);
            return chats;
        } catch (DBException|NumberFormatException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Message> getChatMessages(long chatId, long from, long to, int n) {
        try {
            List<Message> messages = new ArrayList<>();

            long i;
            int direction;
            if (from != -1) {
                i = from;
                direction = 1;
            } else if (to != -1) {
                i = to - 1;
                direction = -1;
            } else {
                byte[] nextIdBytes = levelDBStore.get(bytes(String.format("chat:%d:messages:nextId", chatId)));
                if (nextIdBytes == null) {
                    return new ArrayList<>();
                }
                i = bytesToLong(nextIdBytes) - 1;
                direction = -1;
            }

            byte[] val;
            for (int j = 0;
                     (n <= 0 || j < n) && (from < 0 || i >= from) && (to < 0 || i < to)
                             && (val = levelDBStore.get(bytes((String.format("chat:%d:message:%d:text", chatId, i))))) != null;
                     i += direction, j++) {
                String text = asString(val);
                String timestamp = asString(levelDBStore.get(bytes(String.format("chat:%d:message:%d:timestamp", chatId, i))));
                Long sender = bytesToLong(levelDBStore.get(bytes(String.format("chat:%d:message:%d:sender", chatId, i))));

                if (timestamp == null || sender == null)
                    return null;

                User senderUser = getUser(sender);
                //TODO: Workaround
                senderUser.setPassword(null);

                messages.add(new Message(i, senderUser, Instant.parse(timestamp), text));
            }
            return messages;
        } catch (DBException|DateTimeParseException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> getChatMembers(long chatId) {
        try{
            List<User> members = new ArrayList<>();
            String memberListString = asString(levelDBStore.get(bytes(String.format("chat:%d:members", chatId))));
            if (memberListString == null)
                return null;

            String[] memberStrings = memberListString.split(",");
            for(String memberString : memberStrings){
                if (memberString.equals(""))
                    continue;

                User user = getUser(Long.parseLong(memberString));
                members.add(user);
            }
            return members;
        } catch (DBException|NumberFormatException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addChatMember(long chatId, long userId) {
        WriteBatch batch = null;
        try{
            byte[] keyMemberBytes = bytes(String.format("chat:%d:members", chatId));
            byte[] keyChatsBytes = bytes(String.format("user:%d:chats", userId));

            batch = levelDBStore.createWriteBatch();

            String memberListString = asString(levelDBStore.get(keyMemberBytes));
            if (memberListString == null)
                return false;

            String[] memberStrings = memberListString.split(",");
            List<String> memberStringList = new ArrayList<>(Arrays.asList(memberStrings));
            if (memberStringList.contains(Long.toString(userId)))
                return false;

            memberStringList.add(Long.toString(userId));
            memberListString = String.join(",", memberStringList);
            batch.put(keyMemberBytes, bytes(memberListString));

            String chatListString = asString(levelDBStore.get(keyChatsBytes));
            if (chatListString == null)
                return false;

            List<String> chats = new ArrayList<>(Arrays.asList(chatListString.split(",")));
            if (chats.contains(Long.toString(chatId))){
                return false;
            }
            chats.add(Long.toString(chatId));
            chatListString = String.join(",", chats);
            batch.put(keyChatsBytes, bytes(chatListString));

            levelDBStore.write(batch);

            return true;
        } catch (DBException e){
            e.printStackTrace();
            return false;
        } finally {
            try{
                if (batch != null)
                    batch.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }    }

    @Override
    public boolean removeChatMember(long chatId, long userId) {
        WriteBatch batch = null;
        try{
            byte[] keyMemberBytes = bytes(String.format("chat:%d:members", chatId));
            byte[] keyChatsBytes = bytes(String.format("user:%d:chats", userId));

            batch = levelDBStore.createWriteBatch();

            String memberListString = asString(levelDBStore.get(keyMemberBytes));
            if (memberListString == null)
                return false;

            String[] memberStrings = memberListString.split(",");
            List<String> memberStringList = new ArrayList<>(Arrays.asList(memberStrings));
            if (!memberStringList.contains(Long.toString(userId)))
                return false;

            memberStringList.remove(Long.toString(userId));
            memberListString = String.join(",", memberStringList);
            batch.put(keyMemberBytes, bytes(memberListString));

            String chatListString = asString(levelDBStore.get(keyChatsBytes));
            if (chatListString == null)
                return false;

            List<String> chats = new ArrayList<>(Arrays.asList(chatListString.split(",")));
            if (!chats.contains(Long.toString(chatId))){
                return false;
            }
            chats.remove(Long.toString(chatId));
            chatListString = String.join(",", chats);
            batch.put(keyChatsBytes, bytes(chatListString));

            levelDBStore.write(batch);
            return true;
        } catch (DBException e){
            e.printStackTrace();
            return false;
        } finally {
            try{
                if (batch != null)
                    batch.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean checkChatMember(long chatId, long userId) {
        try{
            byte[] keyBytes = bytes(String.format("chat:%d:members", chatId));
            String memberListString = asString(levelDBStore.get(keyBytes));
            if (memberListString == null)
                return false;

            String[] memberStrings = memberListString.split(",");
            List<String> memberStringList = Arrays.asList(memberStrings);
            return memberStringList.contains(Long.toString(userId));
        } catch (DBException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public long addChatMessage(long chatId, Message message) {
        WriteBatch batch = null;
        try{
            long nextId;
            byte[] nextIdBytes = levelDBStore.get(bytes(String.format("chat:%d:messages:nextId", chatId)));
            if (nextIdBytes == null){
                nextId = 0;
            } else {
                nextId = bytesToLong(nextIdBytes);
            }

            batch = levelDBStore.createWriteBatch();
            batch.put(bytes(String.format("chat:%d:messages:nextId", chatId)), longToBytes(nextId+1));
            batch.put(bytes(String.format("chat:%d:message:%d:text", chatId, nextId)), bytes(message.getText()));
            batch.put(bytes(String.format("chat:%d:message:%d:timestamp", chatId, nextId)), bytes(message.getTimestamp()));
            batch.put(bytes(String.format("chat:%d:message:%d:sender", chatId, nextId)), longToBytes(message.getSender().getUserId()));
            batch.put(bytes(String.format("chat:%d:lastActivity", chatId)), bytes(Instant.now().toString()));
            levelDBStore.write(batch);

            return nextId;
        } catch (DBException e){
            e.printStackTrace();
            return -1;
        } finally {
            try{
                if (batch != null)
                    batch.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public long createChat(String name, long adminId, List<Long> userIds) {
        WriteBatch batch = null;
        try{
            long nextId;
            byte[] nextIdBytes = levelDBStore.get(bytes("chats:nextId"));
            if (nextIdBytes == null){
                nextId = 0;
            } else {
                nextId = bytesToLong(nextIdBytes);
            }

            batch = levelDBStore.createWriteBatch();
            batch.put(bytes("chats:nextId"), longToBytes(nextId+1));
            batch.put(bytes(String.format("chat:%d:name", nextId)), bytes(name));
            batch.put(bytes(String.format("chat:%d:admin", nextId)), longToBytes(adminId));
            batch.put(bytes(String.format("chat:%d:lastActivity", nextId)), bytes(Instant.now().toString()));

            List<String> userStringIds = new ArrayList<>();
            if (!userIds.contains(adminId)){
                userIds.add(adminId);
            }

            for (Long userId:userIds){
                userStringIds.add(Long.toString(userId));

                String chatListString = asString(levelDBStore.get(bytes(String.format("user:%d:chats", userId))));
                if (chatListString == null)
                    return -1;

                List<String> chats = new ArrayList<>(Arrays.asList(chatListString.split(",")));
                chats.add(Long.toString(nextId));
                chatListString = String.join(",", chats);
                batch.put(bytes(String.format("user:%d:chats", userId)), bytes(chatListString));
            }

            batch.put(bytes(String.format("chat:%d:members", nextId)), bytes(String.join(",", userStringIds)));
            levelDBStore.write(batch);

            return nextId;
        } catch (DBException e){
            e.printStackTrace();
            return -1;
        } finally {
            try{
                if (batch != null)
                    batch.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean deleteChat(long chatId) {
        WriteBatch batch = null;
        try{
            batch = levelDBStore.createWriteBatch();
            batch.delete(bytes(String.format("chat:%d:name", chatId)));
            batch.delete(bytes(String.format("chat:%d:admin", chatId)));
            batch.delete(bytes(String.format("chat:%d:lastActivity", chatId)));

            String membersString = asString((levelDBStore.get(bytes(String.format("chat:%d:members", chatId)))));

            List<String> userStringIds = Arrays.asList(membersString.split(","));
            for (String userStringId:userStringIds){
                String chatListString = asString(levelDBStore.get(bytes(String.format("user:%s:chats", userStringId))));
                if (chatListString == null)
                    return false;

                List<String> chats = new ArrayList<>(Arrays.asList(chatListString.split(",")));
                chats.remove(Long.toString(chatId));
                chatListString = String.join(",", chats);
                batch.put(bytes(String.format("user:%s:chats", userStringId)), bytes(chatListString));
            }

            batch.delete(bytes(String.format("chat:%d:members", chatId)));

            for (int i = 0; levelDBStore.get(bytes((String.format("chat:%d:message:%d:text", chatId, i)))) != null; i++){
                batch.delete(bytes((String.format("chat:%d:message:%d:text", chatId, i))));
                batch.delete(bytes((String.format("chat:%d:message:%d:timestamp", chatId, i))));
                batch.delete(bytes((String.format("chat:%d:message:%d:sender", chatId, i))));
            }

            levelDBStore.write(batch);

            return true;
        } catch (DBException e){
            e.printStackTrace();
            return false;
        } finally {
            try{
                if (batch != null)
                    batch.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public Chat getChat(long chatId) {
        try {
            String name = asString(levelDBStore.get(bytes(String.format("chat:%d:name", chatId))));
            Long admin = bytesToLong(levelDBStore.get(bytes(String.format("chat:%d:admin", chatId))));
            String lastActivity = asString(levelDBStore.get(bytes(String.format("chat:%d:lastActivity", chatId))));

            if (name == null || admin == null || lastActivity == null){
                return null;
            }

            Chat chat = new Chat(chatId, name);
            chat.setAdminId(admin);
            chat.setLastActivityInstant(Instant.parse(lastActivity));
            return chat;
        } catch (DBException|DateTimeParseException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long createUser(User user) {
        WriteBatch batch = null;
        try{
            long nextId;
            byte[] nextIdBytes = levelDBStore.get(bytes("users:nextId"));
            if (nextIdBytes == null){
                nextId = 0;
            } else {
                nextId = bytesToLong(nextIdBytes);
            }

            batch = levelDBStore.createWriteBatch();
            batch.put(bytes("users:nextId"), longToBytes(nextId+1));
            batch.put(bytes(String.format("user:%d:username", nextId)), bytes(user.getUsername()));
            batch.put(bytes(String.format("user:%d:password", nextId)), bytes(user.getPassword()));
            batch.put(bytes(String.format("user:%d:chats", nextId)), bytes(""));
            batch.put(bytes(String.format("username:%s:userId", user.getUsername())), longToBytes(nextId));
            levelDBStore.write(batch);

            return nextId;
        } catch (DBException e){
            e.printStackTrace();
            return -1;
        } finally {
            try{
                if (batch != null)
                    batch.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public User getUser(String username) {
        byte[] userIdBytes = null;

        try{
            userIdBytes = levelDBStore.get(bytes(String.format("username:%s:userId", username)));
        } catch (DBException e){
            e.printStackTrace();
        }

        if (userIdBytes == null)
            return null;

        return getUser(bytesToLong(userIdBytes));
    }

    @Override
    public User getUser(long userId) {
        try{
            String username = asString(levelDBStore.get(bytes(String.format("user:%d:username", userId))));
            String password = asString(levelDBStore.get(bytes(String.format("user:%d:password", userId))));
            if (username != null && password != null)
                return new User(userId, username, password);
            else
                return null;
        } catch (DBException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getUserFromSession(String sessionId) {
        try {
            Long userId = bytesToLong(levelDBStore.get(bytes(String.format("session:%s:userId", sessionId))));
            if (userId == null)
                return -1;

            String expiryString = asString(levelDBStore.get(bytes(String.format("session:%s:expiry", sessionId))));
            Instant expiry;
            if (expiryString == null){
                expiry = null;
            } else{
                expiry = Instant.parse(expiryString);
            }

            if (expiry == null || expiry.isBefore(Instant.now())){
                removeSession(sessionId);
                return -1;
            } else
                return userId;
        } catch (DBException|DateTimeParseException e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean setUserSession(UserSession user) {
        WriteBatch batch = null;
        try{
            batch = levelDBStore.createWriteBatch();
            batch.put(bytes(String.format("session:%s:userId", user.getSessionId())), longToBytes(user.getUserId()));
            Instant expiry = Instant.now().plus(5, ChronoUnit.DAYS);
            batch.put(bytes(String.format("session:%s:expiry", user.getSessionId())), bytes(expiry.toString()));
            levelDBStore.write(batch);
            return true;
        } catch (DBException e){
            e.printStackTrace();
            return false;
        } finally {
            try{
                if (batch != null)
                    batch.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean removeSession(String sessionId) {
        WriteBatch batch = null;
        try{
            batch = levelDBStore.createWriteBatch();
            batch.delete(bytes(String.format("session:%s:userId", sessionId)));
            batch.delete(bytes(String.format("session:%s:expiry", sessionId)));
            levelDBStore.write(batch);
            return true;
        } catch (DBException e){
            e.printStackTrace();
            return false;
        } finally {
            try{
                if (batch != null)
                    batch.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean existsChat(long user1, long user2) {
        List<Chat> chats = getChats(user1);
        if (chats == null)
            return false;

        for (Chat chat:chats){
            List<User> members = getChatMembers(chat.getId());
            if (members != null && members.size() == 2 && members.contains(new User(user2)))
                return true;
        }
        return false;
    }

    @Override
    public void close() {
        try {
            levelDBStore.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @see <a href="https://stackoverflow.com/a/29132118">https://stackoverflow.com/a/29132118</a>
     */
    public static Long bytesToLong(byte[] b) {
        if (b == null)
            return null;

        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }


}
