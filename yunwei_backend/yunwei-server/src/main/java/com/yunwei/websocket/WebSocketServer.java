package com.yunwei.websocket;


import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理端来单提醒 WebSocket。
 */
@Slf4j
@Component
@ServerEndpoint("/ws/order")
public class WebSocketServer {

    /**
     * 保存所有在线管理端连接。
     * Set 可以避免同一个浏览器重复建立的 Session 被覆盖。
     */
    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        log.info("管理端WebSocket已连接,当前在线数:{}", SESSIONS.size());
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
        log.info("管理端WebSocket已断开,当前在线数:{}", SESSIONS.size());
    }

    @OnError
    public void OnError(Session session, Throwable error) {
        log.warn("管理端WebSocket连接异常,sessionId:{}", session.getId(), error);
    }


    /**
     * 向全部在线管理端发送来单提醒。
     */
    public void sendToAllClient(String message){
        for(Session session : SESSIONS){
            if(!session.isOpen()){
                SESSIONS.remove(session);
                continue;
            }
            try{
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.warn("发送来单提醒失败,sessionId:{}",session.getId(),e);
            }
        }
    }

}
