package com.frelamape.task0.db.jpa;

import com.frelamape.task0.db.*;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.frelamape.task0.Main.SESSION_DURATION_DAYS;

public class JPAAdapter implements DatabaseAdapter {
    private EntityManagerFactory entityManagerFactory;

    public JPAAdapter(){
        entityManagerFactory = Persistence.createEntityManagerFactory("EasyChat");
    }

    @Override
    public List<Chat> getChats(long userId, boolean loadMembers) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            User user = entityManager.find(User.class, userId);
            for (Chat chat:user.getChats()){
                if (loadMembers)
                    Hibernate.initialize(chat.getMembers());
            }
            List<Chat> chats = user.getChats();
            Collections.sort(chats);
            return chats;
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
        return null;
    }

    @Override
    public List<Message> getChatMessages(long chatId, long from, long to, int n) {
        EntityManager entityManager = null;
        try{
            StringBuilder st = new StringBuilder();
            entityManager = entityManagerFactory.createEntityManager();
            st.append("SELECT M FROM Message M WHERE M.chat = :chat");
            if (from != -1)
                st.append(" AND M.messageId >= :from");
            if (to != -1)
                st.append(" AND M.messageId < :to");
            st.append(" ORDER BY M.messageId ASC");

            Chat chat = entityManager.getReference(Chat.class, chatId);
            if (chat == null)
                return null;

            Query query = entityManager.createQuery(st.toString());
            query.setParameter("chat", chat);
            if (from != -1)
                query.setParameter("from", from);
            if (to != -1)
                query.setParameter("to", to);
            if (n != 0)
                query.setMaxResults(n);
            List<Message> resultList = query.getResultList();

            return resultList;
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                entityManager.close();
            }
        }
        return null;
    }

    @Override
    public List<User> getChatMembers(long chatId) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Chat chat = entityManager.find(Chat.class, chatId);
            if (chat != null){
                Hibernate.initialize(chat.getMembers());
                return chat.getMembers();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
        return null;
    }

    @Override
    public boolean addChatMember(long chatId, long userId) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.getReference(Chat.class, chatId);
            User user = entityManager.getReference(User.class, userId);
            if (chat != null && user != null){
                chat.getMembers().add(user);
                //We do not need merge: entity is NOT detached since entityManager is still open
                entityManager.getTransaction().commit();
                return true;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }
        }
        return false;
    }

    @Override
    public boolean removeChatMember(long chatId, long userId) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.getReference(Chat.class, chatId);
            User user = entityManager.getReference(User.class, userId);
            if (chat != null && user != null){
                chat.getMembers().remove(user);
                entityManager.getTransaction().commit();
                return true;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }
        }
        return false;
    }

    @Override
    public boolean checkChatMember(long chatId, long userId) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Chat chat = entityManager.find(Chat.class, chatId);
            User user = entityManager.find(User.class, userId);
            if (chat != null && user != null){
                return chat.getMembers().contains(user);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
        return false;
    }

    @Override
    public long addChatMessage(long chatId, MessageEntity message) {
        EntityManager entityManager = null;
        Message dbMessage = new Message(chatId, message);
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.getReference(Chat.class, chatId);
            chat.setLastActivity(Timestamp.from(Instant.now()));
            entityManager.merge(chat);
            entityManager.persist(dbMessage);
            entityManager.getTransaction().commit();
            return message.getMessageId();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }
        }
        return -1;
    }

    @Override
    public long createChat(String name, long adminId, List<Long> userIds) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            User admin = entityManager.getReference(User.class, adminId);
            Chat chat = new Chat();
            chat.setName(name);
            chat.setLastActivity(Timestamp.from(Instant.now()));
            if (admin != null){
                chat.setAdmin(admin);
                chat.getMembers().add(admin);
            } else {
                return -1;
            }
            for (Long userId:userIds){
                User member = entityManager.getReference(User.class, userId);
                if (member != null){
                    chat.getMembers().add(member);
                } else {
                    return -1;
                }
            }
            entityManager.persist(chat);
            entityManager.getTransaction().commit();
            return chat.getId();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }
        }
        return -1;
    }

    @Override
    public boolean deleteChat(long chatId) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.getReference(Chat.class, chatId);
            entityManager.remove(chat);
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }
        }
        return false;
    }

    @Override
    public ChatEntity getChat(long chatId, boolean loadMembers) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Chat chat = entityManager.find(Chat.class, chatId);
            if (loadMembers)
                Hibernate.initialize(chat.getMembers());
            return chat;
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                entityManager.close();
            }
        }
        return null;
    }

    @Override
    public long createUser(UserEntity user) {
        EntityManager entityManager = null;
        User dbUser = new User(user);
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(dbUser);
            entityManager.getTransaction().commit();
            return dbUser.getUserId();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }        
        }
        return -1;
    }

    @Override
    public UserEntity getUser(String username){
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username");
            query.setParameter("username", username);
            List<User> resultList = query.getResultList();
            if (!resultList.isEmpty())
                return resultList.get(0);
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                entityManager.close();
            }
        }
        return null;
    }

    @Override
    public UserEntity getUser(long userId){
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            return entityManager.find(User.class, userId);
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                entityManager.close();
            }
        }
        return null;
    }

    @Override
    public long getUserFromSession(String sessionId) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            UserSession session = entityManager.find(UserSession.class, sessionId);
            if(session!=null){
                if (session.getExpiryTimestamp() == null){
                    return -1;
                } else if (session.getExpiry().isBefore(Instant.now())){
                    removeSession(sessionId);
                    return -1;
                } else{
                    return session.getUserId();
                }
            }
            entityManager.getTransaction().commit();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }
        }
        return -1;
    }

    @Override
    public boolean setUserSession(UserSessionEntity sess) {
        EntityManager entityManager = null;
        UserSession dbSession = new UserSession(sess.getUserId(), sess.getSessionId());
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Instant expiry = Instant.now();
            expiry = expiry.plus(SESSION_DURATION_DAYS, ChronoUnit.DAYS);
            dbSession.setExpiry(expiry);
            entityManager.persist(dbSession);
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }        
        }
        return false;
    }

    @Override
    public boolean removeSession(String sessionId) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            UserSession session = entityManager.find(UserSession.class, sessionId);
            if(session!=null) {
                entityManager.remove(session);
            }
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null){
                if (entityManager.getTransaction().isActive())
                    entityManager.getTransaction().rollback();
                entityManager.close();
            }
        }
        return false;
    }

    @Override
    public boolean existsChat(long user1, long user2) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createNativeQuery("SELECT M.chatId\n\n"
                    + "FROM Chatmembers M INNER JOIN Chatmembers M2\n"
                    + "ON M.chatId = M2.chatId\n"
                    + "INNER JOIN Chatmembers M3 ON M3.chatId = M2.chatId\n"
                    + "WHERE M.userId = :user1 AND M2.userId = :user2\n"
                    + "GROUP BY M.chatId\n"
                    + "HAVING COUNT(*) = 2");
            query.setParameter("user1", user1);
            query.setParameter("user2", user2);
            List resultList = query.getResultList();
            return !resultList.isEmpty();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
        return false;
    }

    @Override
    public void close() {
        entityManagerFactory.close();
    }
}
