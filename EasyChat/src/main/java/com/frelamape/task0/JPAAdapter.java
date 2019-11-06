package com.frelamape.task0;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.proxy.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JPAAdapter implements DatabaseAdapter {
    private EntityManagerFactory entityManagerFactory;
    private int connectionId;

    public JPAAdapter(){
        entityManagerFactory = Persistence.createEntityManagerFactory("EasyChat");
        connectionId = 0;
    }

    @Override
    public List<Chat> getChats(long userId) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            User user = entityManager.find(User.class, userId);
            for (Chat chat:user.getChats()){
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

    //TODO: filter from DB instead of programmatically
    @Override
    public List<Message> getChatMessages(long chatId, Instant from, Instant to, int n) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Chat chat = entityManager.find(Chat.class, chatId);
            if (chat != null){
                List<Message> all = chat.getMessages();
                Collections.sort(all);
                if (from == null)
                    Collections.reverse(all);
                List<Message> selected = new ArrayList<>();
                for (Message message : all){
                    if ((from == null || message.getInstantTimestamp().isAfter(from))
                            && (to == null || message.getInstantTimestamp().isBefore(to))){
                        selected.add(message);
                        if (n > 0 && selected.size() >= n) {
                            break;
                        }
                    }
                }
                if (from == null)
                    Collections.reverse(selected);

                return selected;
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
    public long addChatMessage(Message message) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(message);
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
            chat.setLastActivity(new Timestamp(System.currentTimeMillis()));
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
    public Chat getChat(long chatId) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Chat chat = entityManager.find(Chat.class, chatId);
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
    public long createUser(User user) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            return user.getUserId();
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

    public User getUser(String username){
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
    public String getUserDBPassword(String username) {
        User user = getUser(username);
        if (user != null)
            return user.getPassword();
        else
            return null;
    }

    @Override
    public long getUserId(String username) {
        User user = getUser(username);
        if (user != null)
            return user.getUserId();
        else
            return -1;
    }

    @Override
    public long getUserFromSession(String sessionId) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            UserSession session = entityManager.find(UserSession.class, sessionId);
            if(session!=null)
                return session.getUserId();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return -1;
    }

    @Override
    public boolean setUserSession(UserSession sess) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(sess);
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
            UserSession session = entityManager.getReference(UserSession.class, sessionId);
            entityManager.remove(session);
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
    public boolean existsChat(long user, long user2) {
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
            query.setParameter("user1", user);
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
