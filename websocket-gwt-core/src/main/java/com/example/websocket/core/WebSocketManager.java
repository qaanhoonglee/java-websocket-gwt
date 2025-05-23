package com.example.websocket.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lớp quản lý kết nối WebSocket và xử lý callback
 * Lớp này hoạt động như một lớp cơ sở (base) dùng chung cho các views
 */
public class WebSocketManager implements WebSocketCallback {
    
    // WebSocket object
    private WebSocket webSocket;
    private String currentUserName = "";
    private String serverUrl = "ws://localhost:8080/websocket-server/notification-ws";
    private boolean isConnected = false;
    
    // Format thời gian
    private DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm:ss");
    
    // Danh sách listener để thông báo khi có sự kiện WebSocket
    private List<WebSocketListener> listeners = new ArrayList<>();
    
    // Singleton pattern
    private static WebSocketManager instance;
    
    /**
     * Constructor
     */
    private WebSocketManager() {
        // Private constructor để đảm bảo singleton
    }
    
    /**
     * Lấy instance của WebSocketManager
     */
    public static WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }
    
    /**
     * Đặt URL cho WebSocket server
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    /**
     * Lấy trạng thái kết nối hiện tại
     */
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Lấy tên người dùng hiện tại
     */
    public String getCurrentUserName() {
        return currentUserName;
    }
    
    /**
     * Đặt tên người dùng
     */
    public void setCurrentUserName(String userName) {
        this.currentUserName = userName;
    }
    
    /**
     * Thêm listener nhận thông báo sự kiện WebSocket
     */
    public void addListener(WebSocketListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Xóa listener
     */
    public void removeListener(WebSocketListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Kết nối tới WebSocket server
     */
    public void connect() {
        if (webSocket != null) {
            if (webSocket.getReadyState() != WebSocket.CLOSED) {
                GWT.log("WebSocket đã được kết nối hoặc đang kết nối");
                return;
            }
            webSocket = null;
        }
        
        try {
            GWT.log("Đang kết nối tới " + serverUrl);
            webSocket = WebSocket.create(serverUrl);
            webSocket.setOnopen(this);
            webSocket.setOnmessage(this);
            webSocket.setOnclose(this);
            webSocket.setOnerror(this);
        } catch (Exception e) {
            GWT.log("Lỗi kết nối WebSocket: " + e.getMessage());
            notifyError();
        }
    }
    
    /**
     * Ngắt kết nối WebSocket
     */
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close();
        }
    }
    
    /**
     * Gửi tin nhắn thông qua WebSocket
     */
    public void sendMessage(String message) {
        if (isConnected && webSocket != null) {
            try {
                webSocket.send(message);
            } catch (Exception e) {
                GWT.log("Lỗi khi gửi tin nhắn: " + e.getMessage());
            }
        } else {
            GWT.log("Không thể gửi tin nhắn: WebSocket chưa kết nối");
        }
    }
    
    /**
     * Gửi tin nhắn chat với định dạng JSON
     */
    public void sendChatMessage(String content, String recipient) {
        // Tạo tin nhắn JSON
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"type\": \"chat\",");
        jsonBuilder.append("\"sender\": \"").append(escapeJsonString(currentUserName)).append("\",");
        jsonBuilder.append("\"content\": \"").append(escapeJsonString(content)).append("\",");
        
        // Thêm người nhận nếu được chỉ định
        if (recipient != null && !recipient.isEmpty()) {
            jsonBuilder.append("\"recipient\": \"").append(escapeJsonString(recipient)).append("\",");
        }
        
        jsonBuilder.append("\"timestamp\": ").append(System.currentTimeMillis());
        jsonBuilder.append("}");
        
        sendMessage(jsonBuilder.toString());
    }
    
    /**
     * Đăng ký tên người dùng với server
     */
    public void registerUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return;
        }
        
        currentUserName = userName;
        
        // Tạo tin nhắn JSON để đăng ký tên người dùng
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"type\": \"register\",");
        jsonBuilder.append("\"userName\": \"").append(escapeJsonString(userName)).append("\"");
        jsonBuilder.append("}");
        
        sendMessage(jsonBuilder.toString());
    }
    
    /**
     * Gửi ping để kiểm tra kết nối
     */
    public void sendPing() {
        if (isConnected) {
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"type\": \"ping\",");
            jsonBuilder.append("\"timestamp\": ").append(System.currentTimeMillis());
            jsonBuilder.append("}");
            
            sendMessage(jsonBuilder.toString());
        }
    }
    
    // Implementations of WebSocketCallback interface
    
    @Override
    public void onOpen() {
        GWT.log("WebSocket đã kết nối thành công!");
        isConnected = true;
        
        // Nếu đã có tên người dùng, đăng ký với server
        if (currentUserName != null && !currentUserName.isEmpty()) {
            registerUserName(currentUserName);
        }
        
        // Thông báo cho tất cả listeners
        for (WebSocketListener listener : listeners) {
            listener.onConnected();
        }
    }
    
    @Override
    public void onClose(int code, String reason) {
        GWT.log("WebSocket đã đóng: " + code + " - " + reason);
        isConnected = false;
        
        // Thông báo cho tất cả listeners
        for (WebSocketListener listener : listeners) {
            listener.onDisconnected(code, reason);
        }
    }
    
    @Override
    public void onMessage(String message) {
        GWT.log("Nhận tin nhắn WebSocket: " + message);
        
        // Thông báo cho tất cả listeners
        for (WebSocketListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }
    
    @Override
    public void onError() {
        GWT.log("Lỗi WebSocket");
        notifyError();
    }
    
    /**
     * Thông báo lỗi cho tất cả listeners
     */
    private void notifyError() {
        for (WebSocketListener listener : listeners) {
            listener.onError();
        }
    }
    
    /**
     * Phương thức đơn giản để trích xuất giá trị từ một trường trong chuỗi JSON
     * Lưu ý: Đây là một cách thực hiện đơn giản, không phải là một JSON parser đầy đủ
     */
    public static String extractJsonField(String jsonString, String fieldName) {
        int fieldIndex = jsonString.indexOf("\"" + fieldName + "\"");
        if (fieldIndex < 0) {
            return null;
        }
        
        int colonIndex = jsonString.indexOf(":", fieldIndex);
        if (colonIndex < 0) {
            return null;
        }
        
        int valueStartIndex = jsonString.indexOf("\"", colonIndex);
        if (valueStartIndex < 0) {
            // Có thể là số hoặc boolean
            int commaIndex = jsonString.indexOf(",", colonIndex);
            int bracketIndex = jsonString.indexOf("}", colonIndex);
            int endIndex = (commaIndex > 0 && commaIndex < bracketIndex) ? commaIndex : bracketIndex;
            
            if (endIndex > 0) {
                return jsonString.substring(colonIndex + 1, endIndex).trim();
            } else {
                return null;
            }
        }
        
        int valueEndIndex = jsonString.indexOf("\"", valueStartIndex + 1);
        if (valueEndIndex < 0) {
            return null;
        }
        
        return jsonString.substring(valueStartIndex + 1, valueEndIndex);
    }
    
    /**
     * Escape ký tự đặc biệt trong chuỗi JSON
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
