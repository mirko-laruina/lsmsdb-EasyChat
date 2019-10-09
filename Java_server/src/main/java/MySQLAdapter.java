import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLAdapter implements DatabaseAdapter {
    private Connection conn;

    public MySQLAdapter(String connStr){
        try{
            conn = DriverManager.getConnection(connStr);
        } catch (SQLException ex){
            dumpSQLException(ex);
        }
    }

    @Override
    public List<Chat> getChats(String userId) {
        List<Chat> chats = new ArrayList<>();
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT C.chatId, C.name\n"
                        + "FROM Chats C INNER JOIN Chatmembers M ON C.chatId = M.chatId\n"
                        + "WHERE M.userId = ?;"
            );

            statement.setString(1, userId);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                chats.add(new Chat(
                        rs.getLong("C.chatId"),
                        rs.getString("C.name")
                ));
            }
            rs.close();
            statement.close();
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return null;
        }
        return chats;
    }

    @Override
    public List<Message> getChatMessages(long chatId, Date to, int n) {
        List<Message> messages = new ArrayList<>();
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT M.messageId, M.timestamp, M.senderUserId, M.text\n"
                        + "FROM Chats C INNER JOIN Messages M ON C.chatId = M.chatId\n"
                        + "WHERE M.timestamp < ?"
                        + "ORDER BY M.timestamp DESC"
                        + "LIMIT ?"
            );

            statement.setTimestamp(1, new java.sql.Timestamp(to.getTime()));
            statement.setInt(2, n);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                messages.add(new Message(
                        rs.getLong("M.messageId"),
                        chatId,
                        rs.getString("M.senderUserId"),
                        rs.getTimestamp("M.timestamp"),
                        rs.getString("M.text")
                ));
            }
            rs.close();
            statement.close();
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return null;
        }
        return messages;
    }

    @Override
    public List<String> getChatMembers(long chatId) {
        List<String> users = new ArrayList<>();
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT M.userId\n"
                            + "FROM Chats C INNER JOIN Chatmembers M ON C.chatId = M.chatId\n"
                            + "WHERE C.chatId = ?;"
            );

            statement.setLong(1, chatId);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                users.add(rs.getString("M.userId"));
            }
            rs.close();
            statement.close();
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return null;
        }
        return users;
    }

    @Override
    public boolean addChatMember(long chatId, String userId) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Chatmembers(userId, chatId)\n"
                        + "VALUES (?,?);"
            );

            statement.setLong(1, chatId);
            statement.setString(2, userId);
            int rows = statement.executeUpdate();

            statement.close();
            return rows != 0;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return false;
        }
    }

    @Override
    public boolean removeChatMember(long chatId, String userId) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM Chatmembers\n"
                        + "WHERE chatId = ? AND userId = ?;"
            );

            statement.setLong(1, chatId);
            statement.setString(2, userId);
            int rows = statement.executeUpdate();

            statement.close();
            return rows != 0;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return false;
        }
    }

    @Override
    public boolean addChatMessage(Message message) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Messages(chatId, timestamp, senderUserId, text)\n"
                            + "VALUES (?, ?, ?, ?);"
            );

            statement.setLong(1, message.getChatId());
            statement.setTimestamp(2, new Timestamp(message.getTimestamp().getTime()));
            statement.setString(3, message.getUserId());
            statement.setString(4, message.getText());
            boolean result = statement.execute();

            statement.close();
            return result;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return false;
        }
    }

    @Override
    public boolean createChat(String name, String adminId, List<String> userIds) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Chats(name, adminId)\n"
                            + "VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, name);
            statement.setString(2, adminId);

            int rows = statement.executeUpdate();

            if (rows == 0){
                statement.close();
                return false;
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (!generatedKeys.next()){
                statement.close();
                return false;
            }
            long chatId = generatedKeys.getLong(1);

            generatedKeys.close();
            statement.close();

            for (String userId: userIds){
                if (!addChatMember(chatId, userId))
                    return false;
            }

            statement.close();
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteChat(long chatId) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM CHats\n"
                            + "WHERE chatId = ?;"
            );

            statement.setLong(1, chatId);
            int rows = statement.executeUpdate();

            statement.close();
            return rows != 0;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return false;
        }
    }

    @Override
    public boolean createUser(User user) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Users(userId, password)\n"
                            + "VALUES (?,?);"
            );

            statement.setString(1, user.getUserId());
            statement.setString(2, user.getPassword());
            int rows = statement.executeUpdate();

            statement.close();
            return rows != 0;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return false;
        }
    }

    @Override
    public String getUserDBPassword(String userId) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT password\n"
                            + "FROM Users\n"
                            + "WHERE userId = ?;"
            );

            statement.setString(1, userId);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            if(!rs.next()){
                rs.close();
                statement.close();
                return false;
            }

            String dbPassword = rs.getString("password");

            rs.close();
            statement.close();
            return dbPassword;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return null;
        }
    }

    private void dumpSQLException(SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }
}
