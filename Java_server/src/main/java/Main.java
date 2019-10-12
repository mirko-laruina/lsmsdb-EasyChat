import com.google.gson.Gson;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class Main {

    static MySQLAdapter dba = null;

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value={"/api/v1/auth/login"}, method=RequestMethod.POST)
    public @ResponseBody String login(@RequestParam("username") String user, @RequestParam("password") String pw) throws Exception {
        //da rivedere il throws exception
        Gson gson =  new Gson();
        if(user.compareTo("")==0 || pw.compareTo("")==0){
            return gson.toJson(new LoginResult(false, ""));
        }
        String dbPw = dba.getUserDBPassword(user);
        boolean success = (dbPw != null && dbPw.compareTo("") != 0 && dbPw.compareTo(pw) == 0);
        String sid = "";
        if(success){
            sid = generateSessionId();
            long userId = dba.getUserId(user);
            dba.setUserSession(userId, sid);
        }
        LoginResult lr = new LoginResult(success, sid);
        return gson.toJson(lr);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value={"/api/v1/auth/check"}, method=RequestMethod.POST)
    public @ResponseBody String isLogged(@RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        return gson.toJson(new BooleanResult(dba.getUserFromSession(sid) != -1));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value={"/api/v1/chats"}, method=RequestMethod.POST)
    public @ResponseBody String getUserChats(@RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);
        List<Chat> chats= dba.getChats(userId);
        for(Chat chat: chats){
            chat.setMembers(dba.getChatMembers(chat.getId()));
            if(chat.getAdmin() == userId){
                chat.isAdmin = true;
            } else {
                chat.isAdmin = false;
            }
        }
        return gson.toJson(chats);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value={"/api/v1/chat/{chatId}/messages"}, method=RequestMethod.POST)
    public @ResponseBody String getMessages(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        //Check if user has access to that chat
        List<Chat> chats = dba.getChats(userId);
        boolean access = false;
        for (Chat chat: chats) {
            if(chat.getId() == chatId){
                access = true;
                break;
            }
        }

        if(!access){
            //Return empty array
            return gson.toJson(new ArrayList<Message>());
        }

        Date now = new Date();
        List<Message> msgs = dba.getChatMessages(chatId, now, 0);
        return gson.toJson(msgs);
    }


    public String generateSessionId(){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[64];
        random.nextBytes(bytes);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] msgDigest = digest.digest(bytes);
            return bytesToHex(msgDigest);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    //from https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        try {
            dba = new MySQLAdapter("jdbc:mysql://localhost:3306/Task0?user=root&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        } catch (SQLException ex){
            ex.printStackTrace();
            return;
        }
    }

}