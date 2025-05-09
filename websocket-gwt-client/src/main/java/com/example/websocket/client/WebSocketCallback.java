package com.example.websocket.client;

/**
 * Interface callback cho các sự kiện WebSocket
 */
public interface WebSocketCallback {
    
    /**
     * Được gọi khi nhận được tin nhắn từ server
     * @param message Nội dung tin nhắn nhận được
     */
    void onMessage(String message);
    
    /**
     * Được gọi khi kết nối WebSocket được thiết lập
     */
    void onOpen();
    
    /**
     * Được gọi khi kết nối WebSocket đóng
     * @param code Mã đóng kết nối
     * @param reason Lý do đóng kết nối
     */
    void onClose(int code, String reason);
    
    /**
     * Được gọi khi có lỗi xảy ra với kết nối WebSocket
     */
    void onError();
}