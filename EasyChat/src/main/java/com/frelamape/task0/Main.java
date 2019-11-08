package com.frelamape.task0;

import com.google.gson.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class Main {

    public static final int SESSION_DURATION_DAYS = 5;

    static DatabaseAdapter dba = null;

    @CrossOrigin
    @RequestMapping(value={"/api/v1/auth/login"}, method=RequestMethod.POST)
    public @ResponseBody String login(@RequestBody LoginRequest loginRequest) {
        Gson gson =  new Gson();
        LoginResponse lr = new LoginResponse(false, null);

        if(loginRequest.getUsername().equals("") || loginRequest.getPassword().equals("")){
            return gson.toJson(new LoginResponse(false, ""));
        }

        User user = dba.getUser(loginRequest.getUsername());
        if (user != null) {
            String dbPw = user.getPassword();
            boolean success = (dbPw != null && dbPw.equals(loginRequest.getPassword()));
            String sid = "";
            if (success) {
                UserSession session = new UserSession(user);
                if (dba.setUserSession(session)){
                    sid = session.getSessionId();
                    lr = new LoginResponse(success, sid);
                }
            }
        }
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

        List<Chat> chats = dba.getChats(userId);
        if (chats == null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        for (Chat chat:chats){
            chat.setMembers(dba.getChatMembers(chat.getId()));
        }

        GetUserChatsResponse gucr = new GetUserChatsResponse(userId);
        gucr.addAll(chats);

        return new ResponseEntity<>(gson.toJson(gucr.getChats()), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}/messages"}, method=RequestMethod.GET)
    public ResponseEntity getMessages(@PathVariable(value="chatId") long chatId,
                                      @RequestParam("sessionId") String sid,
                                      @RequestParam(name = "from", required = false, defaultValue = "-1") String from,
                                      @RequestParam(name = "to", required = false, defaultValue = "-1") String to,
                                      @RequestParam(name = "n", required = false, defaultValue = "0") String n
                                      ){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        if(userId == -1 || !dba.checkChatMember(chatId, userId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        long fromLong = -1;
        long toLong = -1;
        int nInt = 0;

        try {
            fromLong = Long.parseLong(from);
            toLong = Long.parseLong(to);
            nInt = Integer.parseInt(n);
        } catch (NumberFormatException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Message> msgs = dba.getChatMessages(chatId, fromLong, toLong, nInt);
        return new ResponseEntity<>(gson.toJson(new GetChatMessagesResponse(msgs)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}/messages"}, method=RequestMethod.POST)
    public ResponseEntity sendMessage(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid, @RequestBody SendMessageRequest request){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        if(userId == -1 || !dba.checkChatMember(chatId, userId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //Write message
        User user = new User(userId);
        Message msg = new Message(user, Instant.now(), request.getText().trim());
        long msgId;
        if(msg.getText().length() > 0) {
            msgId = dba.addChatMessage(chatId, msg);
        } else {
            msgId = -1;
        }

        return new ResponseEntity<>(gson.toJson(new BasicResponse(msgId >= 0)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}"}, method=RequestMethod.GET)
    public ResponseEntity getChat(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid){
        Gson gson = new Gson();
        long userId = dba.getUserFromSession(sid);

        if(userId == -1 || !dba.checkChatMember(chatId, userId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //TODO: not very elegant
        Chat chat = dba.getChat(chatId);
        if (chat == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        chat.setMembers(dba.getChatMembers(chatId));
        return new ResponseEntity<>(gson.toJson(new GetChatResponse(chat)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}/member/{memberId}"}, method=RequestMethod.DELETE)
    public ResponseEntity removeChatMember(@PathVariable(value="chatId") long chatId, @PathVariable(value="memberId") long memberId, @RequestParam("sessionId") String sid){
        Gson gson = new Gson();

        long userId = dba.getUserFromSession(sid);
        if (userId == -1){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Chat chat = dba.getChat(chatId);
        if (chat == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (chat.getAdminId() != userId && userId != memberId){
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
        if (userId == -1)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Chat chat = dba.getChat(chatId);
        if (chat == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (chat.getAdminId() != userId){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        long membId = dba.getUser(request.getUsername()).getUserId();
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
            User tmpUser = dba.getUser(memb);
            if(tmpUser != null) {
                long tmpUserId = tmpUser.getUserId();
                if (tmpUserId != -1 && tmpUserId != userId)
                    membIds.add(tmpUserId);
            } else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        if(membIds.size() < 1 || (membIds.size() == 1 && dba.existsChat(userId, membIds.get(0)))){
            return new ResponseEntity<>(gson.toJson(new BasicResponse(false)), HttpStatus.OK);
        }

        long chatId = dba.createChat(request.getName(), userId, membIds);
        return new ResponseEntity<>(gson.toJson(new BasicResponse(chatId >= 0)), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value={"/api/v1/chat/{chatId}"}, method=RequestMethod.DELETE)
    public ResponseEntity deleteChat(@PathVariable(value="chatId") long chatId, @RequestParam("sessionId") String sid){
        Gson gson = new Gson();

        long userId = dba.getUserFromSession(sid);
        if (userId == -1)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Chat chat = dba.getChat(chatId);
        if (chat == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (chat.getAdminId() != userId){
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
        if (userId >= 0){
            UserSession session = new UserSession(userId);
            dba.setUserSession(session);
            sid = session.getSessionId();
        }
        return new ResponseEntity<>(gson.toJson(new LoginResponse(userId >= 0, sid)), HttpStatus.OK);
    }

    public static void main(String[] args) {
        try{
            //TODO: parametrize filename
            dba = new LevelDBAdapter("levelDBStore");
        } catch (IOException e){
            e.printStackTrace();
        }
        SpringApplication.run(Main.class, args);
    }

    @PreDestroy
    public void destroy(){
        dba.close();
    }

}