package com.example.websocket.core;

/**
 * Interface cho các listeners muốn nhận thông báo từ WebSocketManager
 */
public interface WebSocketListener {
    
    /**
     * Được gọi khi kết nối WebSocket được thiết lập thành công
     */
    void onConnected();
    
    /**
     * Được gọi khi kết nối WebSocket bị đóng
     * 
     * @param code Mã đóng kết nối
     * @param reason Lý do đóng kết nối
     */
    void onDisconnected(int code, String reason);
    
    /**
     * Được gọi khi nhận được tin nhắn từ server
     * 
     * @param message Nội dung tin nhắn
     */
    void onMessageReceived(String message);
    
    /**
     * Được gọi khi có lỗi xảy ra với kết nối WebSocket
     */
    void onError();
}
