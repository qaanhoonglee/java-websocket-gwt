package com.example.websocket;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;

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
        
        // Gửi tin nhắn chào mừng
        String welcome = Json.createObjectBuilder()
            .add("type", "welcome")
            .add("message", "Kết nối đến WebSocket server thành công")
            .add("sessionId", session.getId())
            .add("timestamp", System.currentTimeMillis())
            .build().toString();
        
        handler.sendToSession(session, welcome);
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Nhận tin nhắn từ " + session.getId() + ": " + message);
        
        try {
            // Thử phân tích tin nhắn dưới dạng JSON
            JsonReader jsonReader = Json.createReader(new StringReader(message));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            
            String type = jsonObject.getString("type", "");
            
            // Xử lý theo loại tin nhắn
            switch (type) {
                case "chat":
                    String content = jsonObject.getString("content", "");
                    String response = Json.createObjectBuilder()
                        .add("type", "chat")
                        .add("sender", session.getId())
                        .add("content", content)
                        .add("timestamp", System.currentTimeMillis())
                        .build().toString();
                    
                    // Gửi đến tất cả các client
                    handler.broadcastMessage(response);
                    break;
                    
                case "ping":
                    String pong = Json.createObjectBuilder()
                        .add("type", "pong")
                        .add("timestamp", System.currentTimeMillis())
                        .build().toString();
                    
                    // Gửi chỉ cho client gọi
                    handler.sendToSession(session, pong);
                    break;
                    
                default:
                    // Nếu không phải định dạng JSON hoặc không có trường type
                    handler.handleMessage(message, session);
                    break;
            }
        } catch (Exception e) {
            // Nếu không phải định dạng JSON, xử lý như tin nhắn thông thường
            handler.handleMessage(message, session);
        }
    }
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("Kết nối đóng: " + session.getId());
        handler.removeSession(session);
        
        // Thông báo cho các client khác
        String notification = Json.createObjectBuilder()
            .add("type", "system")
            .add("message", "Client " + session.getId() + " đã ngắt kết nối")
            .add("timestamp", System.currentTimeMillis())
            .build().toString();
            
        handler.broadcastMessage(notification);
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Lỗi xảy ra tại session " + session.getId());
        throwable.printStackTrace();
    }
}