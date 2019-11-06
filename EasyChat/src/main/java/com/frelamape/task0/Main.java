package com.frelamape.task0;

import com.google.gson.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
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
            return gson.toJson(new LoginResponse(false, ""));
        }
        String dbPw = dba.getUserDBPassword(loginRequest.getUsername());
        boolean success = (dbPw != null && dbPw.equals(loginRequest.getPassword()));
        String sid = "";
        if(success){
            User user = dba.getUser(loginRequest.getUsername());
            UserSession session = new UserSession(user);
            dba.setUserSession(session);
            sid = session.getSessionId();
        }
        LoginResponse lr = new LoginResponse(success, sid);
        return gson.toJson(lr);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/auth/check"}, method=RequestMethod.GET)
    public @ResponseBody String isLogged(@RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        return gson.toJson(new BasicResponse(dba.getUserFromSession(sid) != -1));
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/auth/logout"}, method=RequestMethod.POST)
    public @ResponseBody String logout(@RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        return gson.toJson(new BasicResponse(dba.removeSession(sid)));
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chats"}, method=RequestMethod.GET)
    public ResponseEntity getUserChats(@RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);
        if (userId == -1){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        GetUserChatsResponse gucr = dba.getChats(userId);

        return new ResponseEntity<>(gson.toJson(gucr.getChats()), HttpStatus.OK);
//        for(Chat chat: chats){
//            chat.setMembers(dba.getChatMembers(chat.getId()));
//            if(chat.getAdmin().getUserId() == userId){
//                chat.isAdmin = true;
//            } else {
//                chat.isAdmin = false;
//            }
//        }
        //return null;
        //return new ResponseEntity<>(gson.toJson(chats), HttpStatus.OK);
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
        return new ResponseEntity<>(gson.toJson(new GetChatMessagesResponse(msgs)), HttpStatus.OK);
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
        Chat chat = new Chat(chatId);
        Message msg = new Message(chat, user, Instant.now(), request.getText().trim());
        long msgId;
        if(msg.getText().length() > 0) {
            msgId = dba.addChatMessage(msg);
        } else {
            msgId = 0;
        }

        return new ResponseEntity<>(gson.toJson(new BasicResponse(msgId > 0)), HttpStatus.OK);
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
        if (chat.getAdmin().getUserId() != userId && userId != memberId){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean result;
        if(dba.getChatMembers(chatId).size() <= 3){
            result = false;
        } else {
            result = dba.removeChatMember(chatId, memberId);
        }

        return new ResponseEntity<>(gson.toJson(new BasicResponse(result)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}/members"}, method=RequestMethod.POST)
    public ResponseEntity addChatMember(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid, @RequestBody AddMemberRequest request){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        Chat chat = dba.getChat(chatId);
        if (chat.getAdmin().getUserId() != userId){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        long membId = dba.getUserId(request.getUsername());
        boolean result = (membId != -1) && dba.addChatMember(chatId, membId);
        return new ResponseEntity<>(gson.toJson(new BasicResponse(result)), HttpStatus.OK);
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
            return new ResponseEntity<>(gson.toJson(new BasicResponse(false)), HttpStatus.OK);
        }

        long chatId = dba.createChat(request.getName(), userId, membIds);
        return new ResponseEntity<>(gson.toJson(new BasicResponse(chatId > 0)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}"}, method=RequestMethod.DELETE)
    public ResponseEntity deleteChat(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        Chat chat = dba.getChat(chatId);
        if (chat != null && chat.getAdmin().getUserId() != userId){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean result = dba.deleteChat(chatId);
        return new ResponseEntity<>(gson.toJson(new BasicResponse(result)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/users"}, method=RequestMethod.POST)
    public ResponseEntity registerUser(@RequestBody LoginRequest request){
        Gson gson = new Gson();
        long userId = dba.createUser(new User(request.getUsername(), request.getPassword()));
        String sid = "";
        if (userId > 0){
            UserSession session = new UserSession(userId);
            dba.setUserSession(session);
        }
        return new ResponseEntity<>(gson.toJson(new LoginResponse(userId > 0, sid)), HttpStatus.OK);
    }

    public static void main(String[] args) {
        String propFileName = null;
        dba = new JPAAdapter();

        SpringApplication.run(Main.class, args);
    }

    @PreDestroy
    public void destroy(){
        dba.close();
    }

}