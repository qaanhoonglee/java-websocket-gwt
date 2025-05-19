package com.example.websocket.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point cho ứng dụng GWT WebSocket Client
 * Lớp này chỉ khởi tạo UI và hiển thị ChatView
 */
public class WebSocketClientApp implements EntryPoint {
    /**
     * Entry point method - được gọi khi ứng dụng GWT khởi động
     */
    public void onModuleLoad() {
        // Khởi tạo WebSocketManager
        WebSocketManager.getInstance();
        
        // Tạo và hiển thị view chat
        ChatView chatView = new ChatView();
        RootLayoutPanel.get().add(chatView);
    }
}