public long addChatMessage(Message message) {
        EntityManager entityManager = null;
        try{
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            Chat chat = entityManager.getReference(Chat.class, message.getChat().getId());
            chat.setLastActivity(Timestamp.from(Instant.now()));
            entityManager.merge(chat);
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