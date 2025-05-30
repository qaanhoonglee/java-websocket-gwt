package com.example.websocket;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.json.Json;
import javax.websocket.Session;

/**
 * Lớp SocketHandler sử dụng mẫu Singleton để quản lý các kết nối WebSocket
 * và xử lý tin nhắn tương tự như phiên bản Spring Boot
 */
public class SocketHandler {
    
    private static final List<Session> sessions = new CopyOnWriteArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
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
        
        // Tạo tin nhắn phản hồi dạng JSON với timestamp dạng số cho tính nhất quán
        String response = Json.createObjectBuilder()
            .add("type", "message")
            .add("from", "server")
            .add("content", message)
            .add("timestamp", System.currentTimeMillis())
            .add("formattedTime", LocalDateTime.now().format(formatter))
            .build().toString();
        
        broadcastMessage(response);
    }
    
    // Gửi tin nhắn đến một session cụ thể
    public void sendToSession(Session session, String message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message);
                System.out.println("Đã gửi tin nhắn đến session " + session.getId());
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi gửi tin nhắn đến session " + session.getId());
            e.printStackTrace();
        }
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
                System.err.println("Lỗi khi phát tin nhắn đến session " + session.getId());
                e.printStackTrace();
            }
        }
    }
      // Gửi thông tin trạng thái server cho tất cả clients
    public void broadcastStatus() {
        String status = Json.createObjectBuilder()
            .add("type", "status")
            .add("connections", sessions.size())
            .add("timestamp", System.currentTimeMillis())
            .add("formattedTime", LocalDateTime.now().format(formatter))
            .build().toString();
            
        broadcastMessage(status);
    }
}