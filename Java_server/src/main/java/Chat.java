import java.util.List;

public class Chat {
    private long id;
    private String name;
    private List<Message> messages;
    private List<User> members;
    private User admin;

    public Chat(int id, String name, List<Message> messages, List<User> members, User admin) {
        this.id = id;
        this.name = name;
        this.messages = messages;
        this.members = members;
        this.admin = admin;
    }

    public Chat(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}
