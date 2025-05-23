package com.example.websocket.client.manager;

import com.example.websocket.core.WebSocketManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point cho ứng dụng GWT WebSocket Client Manager
 * Lớp này khởi tạo UI và hiển thị ManagerView
 */
public class WebSocketClientManagerApp implements EntryPoint {

    /**
     * Entry point method - được gọi khi ứng dụng GWT khởi động
     */
    public void onModuleLoad() {
        // Khởi tạo WebSocketManager và đăng ký tên người dùng mặc định
        WebSocketManager webSocketManager = WebSocketManager.getInstance();
        webSocketManager.setCurrentUserName("user2");

        // Kết nối tới WebSocket server
        webSocketManager.connect();

        // Tạo và hiển thị view quản lý
        ManagerView managerView = new ManagerView();
        RootLayoutPanel.get().add(managerView);
    }
}
