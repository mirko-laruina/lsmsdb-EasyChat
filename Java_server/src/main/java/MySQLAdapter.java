import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLAdapter implements DatabaseAdapter {
    private Connection conn;

    public MySQLAdapter(String connStr) throws SQLException{
        try{
            conn = DriverManager.getConnection(connStr);
        } catch (SQLException ex){
            dumpSQLException(ex);
            throw ex;
        }
    }

    @Override
    public List<Chat> getChats(long userId) {
        List<Chat> chats = new ArrayList<>();
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT C.chatId, C.name\n"
                        + "FROM Chats C INNER JOIN Chatmembers M ON C.chatId = M.chatId\n"
                        + "WHERE M.userId = ?;"
            );

            statement.setLong(1, userId);
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
                    "SELECT M.messageId, M.timestamp, U.userId, U.username, M.text\n"
                        + "FROM Chats C INNER JOIN Messages M ON C.chatId = M.chatId\n"
                        + "INNER JOIN Users U on U.userId = M.senderUserId\n"
                        + "WHERE M.timestamp < ?\n"
                        + "ORDER BY M.timestamp DESC"
                        + (n == 0 ? ";" : "\nLIMIT ?;")
            );

            statement.setTimestamp(1, new java.sql.Timestamp(to.getTime()));
            if (n != 0)
                statement.setInt(2, n);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                messages.add(new Message(
                        rs.getLong("M.messageId"),
                        chatId,
                        new User(rs.getLong("U.userId"), rs.getString("U.username")),
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
    public List<User> getChatMembers(long chatId) {
        List<User> users = new ArrayList<>();
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT M.userId, U.username\n"
                            + "FROM Chats C INNER JOIN Chatmembers M ON C.chatId = M.chatId\n"
                            + "INNER JOIN Users U ON U.userId = M.userId\n"
                            + "WHERE C.chatId = ?;"
            );

            statement.setLong(1, chatId);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                users.add(new User(rs.getLong("M.userId"), rs.getString("U.username")));
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
    public boolean addChatMember(long chatId, long userId) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Chatmembers(chatId, userId)\n"
                        + "VALUES (?,?);"
            );

            statement.setLong(1, chatId);
            statement.setLong(2, userId);
            int rows = statement.executeUpdate();

            statement.close();
            return rows != 0;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return false;
        }
    }

    @Override
    public boolean removeChatMember(long chatId, long userId) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM Chatmembers\n"
                        + "WHERE chatId = ? AND userId = ?;"
            );

            statement.setLong(1, chatId);
            statement.setLong(2, userId);
            int rows = statement.executeUpdate();

            statement.close();
            return rows != 0;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return false;
        }
    }

    @Override
    public long addChatMessage(Message message) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Messages(chatId, timestamp, senderUserId, text)\n"
                            + "VALUES (?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setLong(1, message.getChatId());
            statement.setTimestamp(2, new Timestamp(message.getTimestamp().getTime()));
            statement.setLong(3, message.getSender().getUserId());
            statement.setString(4, message.getText());
            int rows = statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (!generatedKeys.next()){
                statement.close();
                return -1;
            }
            long messageId = generatedKeys.getLong(1);

            generatedKeys.close();

            statement.close();
            return messageId;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return -1;
        }
    }

    @Override
    public long createChat(String name, long adminId, List<Long> userIds) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Chats(name, adminId)\n"
                            + "VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, name);
            statement.setLong(2, adminId);

            int rows = statement.executeUpdate();

            if (rows == 0){
                statement.close();
                return -1;
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (!generatedKeys.next()){
                statement.close();
                return -1;
            }
            long chatId = generatedKeys.getLong(1);

            generatedKeys.close();
            statement.close();

            for (long userId: userIds){
                if (!addChatMember(chatId, userId))
                    return -1;
            }

            statement.close();

            return chatId;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return -1;
        }
    }

    @Override
    public boolean deleteChat(long chatId) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM Chats\n"
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
    public long createUser(User user) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO Users(userId, username, password)\n"
                            + "VALUES (?,?,?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setLong(1, user.getUserId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            int rows = statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (!generatedKeys.next()){
                statement.close();
                return -1;
            }
            long userId = generatedKeys.getLong(1);

            generatedKeys.close();

            statement.close();
            return userId;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return -1;
        }
    }

    @Override
    public String getUserDBPassword(String username) {
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT password\n"
                            + "FROM Users\n"
                            + "WHERE username = ?;"
            );

            statement.setString(1, username);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            if(!rs.next()){
                rs.close();
                statement.close();
                return null;
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

    @Override
    public long getUserId(String username){
        try{
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT userId\n"
                            + "FROM Users\n"
                            + "WHERE username = ?;"
            );

            statement.setString(1, username);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            if(!rs.next()){
                rs.close();
                statement.close();
                return -1;
            }

            long dbUserId = rs.getLong("userId");

            rs.close();
            statement.close();
            return dbUserId;
        } catch(SQLException ex) {
            dumpSQLException(ex);
            return -1;
        }
    }

    private void dumpSQLException(SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }
}
