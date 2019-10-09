import java.util.Date;
import java.util.List;

public interface DatabaseAdapter {
    List<Chat> getChats(String userId);
    List<Message> getChatMessages(long chatId, Date to, int n);
    List<String> getChatMembers(long chatId);
    boolean addChatMember(long chatId, String userId);
    boolean removeChatMember(long chatId, String userId);
    boolean addChatMessage(Message message);
    boolean createChat(String name, String adminId, List<String> userIds);
    boolean deleteChat(long chatId);
    boolean createUser(User user);
    String getUserDBPassword(String userId);
}
