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