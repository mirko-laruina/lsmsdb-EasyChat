import java.util.Date;

public class Message {
    private long id;
    private long chatId;
    private String userId;
    private Date timestamp;
    private String text;

    public Message(long id, long chatId, String userId, Date timestamp, String text) {
        this.id = id;
        this.chatId = chatId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
