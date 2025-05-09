package com.example.websocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket Endpoint để xử lý kết nối và tin nhắn
 * Sử dụng annotation @ServerEndpoint để đăng ký endpoint WebSocket
 */
@ServerEndpoint(value="/notification-ws")
public class WebSocketEndpoint {
    
    private final SocketHandler handler = SocketHandler.getInstance();
    
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Kết nối mới được thiết lập: " + session.getId());
        handler.addSession(session);
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        handler.handleMessage(message, session);
    }
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("Kết nối đóng: " + session.getId());
        handler.removeSession(session);
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Lỗi xảy ra tại session " + session.getId());
        throwable.printStackTrace();
    }
}