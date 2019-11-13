package com.frelamape.task0;

import com.frelamape.task0.api.GetChatMessagesResponse;
import com.frelamape.task0.db.Message;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WaitManager {
    private Map<Long, List<DeferredResult>> chatList = new HashMap<>();

    public void add(DeferredResult dr, long chatId){
        chatList.computeIfAbsent(chatId, k -> new ArrayList<>());
        chatList.get(chatId).add(dr);
    }

    public void wakeup(long chatId, Object o){
        List<DeferredResult> cl = chatList.get(chatId);
        if(cl == null){
            return;
        }
        for (DeferredResult dr: cl){
            Gson gson = new Gson();
            dr.setResult(new ResponseEntity<>(gson.toJson(o), HttpStatus.OK));
        }
        chatList.put(chatId, null);
    }
}
