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