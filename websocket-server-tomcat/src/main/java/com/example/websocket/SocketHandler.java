package com.example.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.websocket.Session;

/**
 * Lớp SocketHandler sử dụng mẫu Singleton để quản lý các kết nối WebSocket
 * và xử lý tin nhắn tương tự như phiên bản Spring Boot
 */
public class SocketHandler {
    
    private static final List<Session> sessions = new CopyOnWriteArrayList<>();
    
    // Singleton pattern để dễ dàng truy cập từ bất kỳ đâu
    private static final SocketHandler INSTANCE = new SocketHandler();
    
    private SocketHandler() {
        // Private constructor để ngăn tạo instance mới
    }
    
    public static SocketHandler getInstance() {
        return INSTANCE;
    }
    
    // Quản lý sessions
    public void addSession(Session session) {
        sessions.add(session);
        System.out.println("Session mới được thêm: " + session.getId() + ", tổng số kết nối: " + sessions.size());
    }
    
    public void removeSession(Session session) {
        sessions.remove(session);
        System.out.println("Session đã được xóa: " + session.getId() + ", tổng số kết nối còn lại: " + sessions.size());
    }
    
    // Xử lý tin nhắn đến
    public void handleMessage(String message, Session session) {
        System.out.println("Tin nhắn từ " + session.getId() + ": " + message);
        broadcastMessage("Nhận tin nhắn: " + message);
    }
    
    // Phát tin nhắn đến tất cả clients
    public void broadcastMessage(String message) {
        System.out.println("Phát tin nhắn đến tất cả clients: " + message);
        for (Session session : sessions) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}