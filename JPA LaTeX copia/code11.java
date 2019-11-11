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