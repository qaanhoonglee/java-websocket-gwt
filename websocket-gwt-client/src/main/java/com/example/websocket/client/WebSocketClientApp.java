package com.example.websocket.client;

import com.example.websocket.core.WebSocketManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point cho ứng dụng GWT WebSocket Client
 * Lớp này khởi tạo UI, đăng ký username và hiển thị ChatView
 */
public class WebSocketClientApp implements EntryPoint {
    // Tên người dùng mặc định
    private static final String DEFAULT_USERNAME = "user1";
    
    /**
     * Entry point method - được gọi khi ứng dụng GWT khởi động
     */
    public void onModuleLoad() {
        // Khởi tạo WebSocketManager
        WebSocketManager webSocketManager = WebSocketManager.getInstance();
        
        // Đặt tên người dùng
        webSocketManager.setCurrentUserName(DEFAULT_USERNAME);
        
        // Kết nối tới WebSocket server
        webSocketManager.connect();
        
        // Tạo và hiển thị view chat
        ChatView chatView = new ChatView();
        RootLayoutPanel.get().add(chatView);
    }
}