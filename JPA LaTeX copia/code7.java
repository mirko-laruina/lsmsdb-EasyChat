public JPAAdapter(){
        entityManagerFactory = Persistence.createEntityManagerFactory("EasyChat");
        connectionId = 0;
    }