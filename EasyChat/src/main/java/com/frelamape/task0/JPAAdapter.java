package com.frelamape.task0;

import org.hibernate.Criteria;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JPAAdapter implements DatabaseAdapter {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private int connectionId;

    public JPAAdapter(){
        entityManagerFactory = Persistence.createEntityManagerFactory("EasyChat");
        connectionId = 0;
    }

    @Override
    public List<Chat> getChats(long userId) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            User user = entityManager.find(User.class, userId);
            if (user != null){
                return user.getChats();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return null;
    }

    //TODO: filter from DB instead of programmatically
    @Override
    public List<Message> getChatMessages(long chatId, Instant from, Instant to, int n) {
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
                if (from != null)
                    Collections.reverse(selected);
                return selected;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return null;
    }

    @Override
    public List<User> getChatMembers(long chatId) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Chat chat = entityManager.find(Chat.class, chatId);
            if (chat != null){
                return chat.getMembers();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return null;
    }

    @Override
    public boolean addChatMember(long chatId, long userId) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.getReference(Chat.class, chatId);
            User user = entityManager.getReference(User.class, userId);
            boolean ret = false;
            if (chat != null && user != null){
                chat.getMembers().add(user);
                ret = true;
            }
            entityManager.getTransaction().commit();
            return ret;
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return false;
    }

    @Override
    public boolean removeChatMember(long chatId, long userId) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.getReference(Chat.class, chatId);
            User user = entityManager.getReference(User.class, userId);
            boolean ret = false;
            if (chat != null && user != null){
                chat.getMembers().remove(user);
                ret = true;
            }
            entityManager.getTransaction().commit();
            return ret;
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return false;
    }

    @Override
    public boolean checkChatMember(long chatId, long userId) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.find(Chat.class, chatId);
            User user = entityManager.find(User.class, userId);
            entityManager.getTransaction().commit();
            if (chat != null && user != null){
                return chat.getMembers().contains(user);
            }
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return false;
    }

    @Override
    public long addChatMessage(Message message) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(message);
            entityManager.getTransaction().commit();
            return message.getMessageId();
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return -1;
    }

    @Override
    public long createChat(String name, long adminId, List<Long> userIds) {
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
                entityManager.getTransaction().setRollbackOnly();
                return -1;
            }
            for (Long userId:userIds){
                User member = entityManager.getReference(User.class, userId);
                if (member != null){
                    chat.getMembers().add(member);
                } else {
                    entityManager.getTransaction().setRollbackOnly();
                    return -1;
                }
            }
            entityManager.persist(chat);
            entityManager.getTransaction().commit();
            return chat.getId();
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            if(entityManager.getTransaction().getRollbackOnly()){
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
        return -1;
    }

    @Override
    public boolean deleteChat(long chatId) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.getReference(Chat.class, chatId);
            entityManager.remove(chat);
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return false;
    }

    @Override
    public Chat getChat(long chatId) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            Chat chat = entityManager.find(Chat.class, chatId);
            return chat;
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return null;
    }

    @Override
    public long createUser(User user) {
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            return user.getUserId();
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            entityManager.close();
        }
        return -1;
    }

    public User getUser(String username){
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username");
            query.setParameter("username", username);
            List<User> resultList = query.getResultList();
            entityManager.getTransaction().commit();
            if (!resultList.isEmpty())
                return resultList.get(0);
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            entityManager.close();
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
        return 0;
    }

    @Override
    public boolean setUserSession(long userId, String sessionId) {
        return false;
    }

    @Override
    public boolean removeSession(String sessionId) {
        return false;
    }

    @Override
    public boolean existsChat(long user, long user2) {
        return false;
    }

    @Override
    public void close() {
        entityManagerFactory.close();
    }
}
