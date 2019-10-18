package com.frelamape.task0;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class Main {

    static DatabaseAdapter dba = null;

    @CrossOrigin
    @RequestMapping(value={"/api/v1/auth/login"}, method=RequestMethod.POST)
    public @ResponseBody String login(@RequestBody LoginRequest loginRequest) throws Exception {
        //da rivedere il throws exception
        Gson gson =  new Gson();
        if(loginRequest.getUsername().equals("") || loginRequest.getPassword().equals("")){
            return gson.toJson(new LoginResult(false, ""));
        }
        String dbPw = dba.getUserDBPassword(loginRequest.getUsername());
        boolean success = (dbPw != null && dbPw.equals(loginRequest.getPassword()));
        String sid = "";
        if(success){
            sid = generateSessionId();
            long userId = dba.getUserId(loginRequest.getUsername());
            dba.setUserSession(userId, sid);
        }
        LoginResult lr = new LoginResult(success, sid);
        return gson.toJson(lr);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/auth/check"}, method=RequestMethod.GET)
    public @ResponseBody String isLogged(@RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        return gson.toJson(new BooleanResult(dba.getUserFromSession(sid) != -1));
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/auth/logout"}, method=RequestMethod.POST)
    public @ResponseBody String logout(@RequestParam("sessionId") String sid){
        Gson gson = new Gson();

        return gson.toJson(new BooleanResult(dba.removeSession(sid)));
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chats"}, method=RequestMethod.GET)
    public ResponseEntity getUserChats(@RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);
        if (userId == -1){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Chat> chats= dba.getChats(userId);
        for(Chat chat: chats){
            chat.setMembers(dba.getChatMembers(chat.getId()));
            if(chat.getAdmin() == userId){
                chat.isAdmin = true;
            } else {
                chat.isAdmin = false;
            }
        }
        return new ResponseEntity<>(gson.toJson(chats), HttpStatus.OK);
    }

    //TODO support parameters
    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}/messages"}, method=RequestMethod.GET)
    public ResponseEntity getMessages(@PathVariable(value="chatId") long chatId,
                                      @RequestParam("sessionId") String sid,
                                      @RequestParam(name = "from", required = false, defaultValue = "0") String from,
                                      @RequestParam(name = "to", required = false, defaultValue = "0") String to,
                                      @RequestParam(name = "n", required = false, defaultValue = "0") String n
                                      ){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        if(!dba.checkChatMember(chatId, userId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Instant fromInstant = null;
        Instant toInstant = null;
        int nInt = 0;

        try {
            long fromLong = Long.parseLong(from);
            if (fromLong != 0)
                fromInstant = Instant.ofEpochMilli(fromLong);

            long toLong = Long.parseLong(to);
            if (toLong != 0)
                toInstant = Instant.ofEpochMilli(toLong);

            nInt = Integer.parseInt(n);
        } catch (NumberFormatException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Message> msgs = dba.getChatMessages(chatId, fromInstant, toInstant, nInt);
        return new ResponseEntity<>(gson.toJson(msgs), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}/messages"}, method=RequestMethod.POST)
    public ResponseEntity sendMessage(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid, @RequestBody SendMessageRequest request){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);
        if(userId == -1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!dba.checkChatMember(chatId, userId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //Write message
        User user = new User(userId);
        Message msg = new Message(chatId, user, Instant.now(), request.getText().trim());
        long msgId;
        if(msg.getText().length() > 0) {
            msgId = dba.addChatMessage(msg);
        } else {
            msgId = 0;
        }

        return new ResponseEntity<>(gson.toJson(new BooleanResult(msgId > 0)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}"}, method=RequestMethod.GET)
    public ResponseEntity getChat(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        if(!dba.checkChatMember(chatId, userId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Chat chat = dba.getChat(chatId);
        chat.setMembers(dba.getChatMembers(chatId));
        return new ResponseEntity<>(gson.toJson(chat), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}/member/{memberId}"}, method=RequestMethod.DELETE)
    public ResponseEntity removeChatMember(@PathVariable(value="chatId") long chatId, @PathVariable(value="memberId") long memberId, @RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        Chat chat = dba.getChat(chatId);
        if (chat.getAdmin() != userId && userId != memberId){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean result;
        if(dba.getChatMembers(chatId).size() <= 3){
            result = false;
        } else {
            result = dba.removeChatMember(chatId, memberId);
        }

        return new ResponseEntity<>(gson.toJson(new BooleanResult(result)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}/members"}, method=RequestMethod.POST)
    public ResponseEntity addChatMember(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid, @RequestBody AddMemberRequest request){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        Chat chat = dba.getChat(chatId);
        if (chat.getAdmin() != userId){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        long membId = dba.getUserId(request.getUsername());
        boolean result = (membId != -1) && dba.addChatMember(chatId, membId);
        return new ResponseEntity<>(gson.toJson(new BooleanResult(result)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chats"}, method=RequestMethod.POST)
    public ResponseEntity createChat(@RequestParam("sessionId") String sid, @RequestBody CreateChatRequest request){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);
        if (userId == -1){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<String> members = request.getMembers();
        List<Long> membIds = new ArrayList<Long>();
        for(String memb: members){
            long tmpUserId = dba.getUserId(memb);
            if(tmpUserId != -1 && tmpUserId != userId)
                membIds.add(dba.getUserId(memb));
        }

        if(membIds.size() < 1 || (membIds.size() == 1 && dba.existsChat(userId, membIds.get(0)))){
            return new ResponseEntity<>(gson.toJson(new BooleanResult(false)), HttpStatus.OK);
        }

        long chatId = dba.createChat(request.getName(), userId, membIds);
        return new ResponseEntity<>(gson.toJson(new BooleanResult(chatId > 0)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}"}, method=RequestMethod.DELETE)
    public ResponseEntity deleteChat(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        Chat chat = dba.getChat(chatId);
        if (chat != null && chat.getAdmin() != userId){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean result = dba.deleteChat(chatId);
        return new ResponseEntity<>(gson.toJson(new BooleanResult(result)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/users"}, method=RequestMethod.POST)
    public ResponseEntity registerUser(@RequestBody LoginRequest request){
        Gson gson = new Gson();
        long userId = dba.createUser(new User(request.getUsername(), request.getPassword()));
        String sid = "";
        if (userId > 0){
            sid = generateSessionId();
            dba.setUserSession(userId, sid);
        }
        return new ResponseEntity<>(gson.toJson(new LoginResult(userId > 0, sid)), HttpStatus.OK);
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
        String propFileName = null;
        try {
            if (args.length > 0)
                propFileName = args[0];
            else
                propFileName = "server.config";
            Settings settings = new Settings(propFileName);
            String connStr = String.format(settings.getConnStr());
            dba = new MySQLAdapter(connStr);
        } catch (SQLException ex){
            ex.printStackTrace();
            return;
        } catch (FileNotFoundException e){
            if (propFileName != null)
                System.err.println(String.format("File not found: %s", propFileName));
            return;
        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        SpringApplication.run(Main.class, args);
    }

    @PreDestroy
    public void destroy(){
        dba.close();
    }

}