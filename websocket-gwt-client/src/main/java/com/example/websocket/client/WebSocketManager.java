package com.example.websocket.client;

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
            disconnect();
        }
        
        try {
            GWT.log("Đang kết nối tới " + serverUrl);
            webSocket = WebSocket.create(serverUrl);
            
            // Đăng ký callback
            webSocket.setOnopen(this);
            webSocket.setOnclose(this);
            webSocket.setOnmessage(this);
            webSocket.setOnerror(this);
        } catch (Exception e) {
            GWT.log("Lỗi khi tạo kết nối WebSocket: " + e.getMessage());
            notifyError();
        }
    }
    
    /**
     * Ngắt kết nối WebSocket
     */
    public void disconnect() {
        if (webSocket != null) {
            try {
                webSocket.close();
            } catch (Exception e) {
                GWT.log("Lỗi khi đóng kết nối WebSocket: " + e.getMessage());
            } finally {
                webSocket = null;
                isConnected = false;
            }
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
                notifyError();
            }
        } else {
            GWT.log("Không thể gửi tin nhắn: WebSocket không được kết nối");
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
        
        // Thêm người nhận nếu có
        if (recipient != null && !recipient.isEmpty()) {
            jsonBuilder.append("\"recipient\": \"").append(escapeJsonString(recipient)).append("\",");
        }
        
        jsonBuilder.append("\"timestamp\": ").append(new Date().getTime());
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
        
        this.currentUserName = userName.trim();
        
        // Tạo tin nhắn JSON để đăng ký tên
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"type\": \"register\",");
        jsonBuilder.append("\"userName\": \"").append(escapeJsonString(currentUserName)).append("\",");
        jsonBuilder.append("\"timestamp\": ").append(new Date().getTime());
        jsonBuilder.append("}");
        
        sendMessage(jsonBuilder.toString());
    }
    
    /**
     * Gửi ping để kiểm tra kết nối
     */
    public void sendPing() {
        // Tạo tin nhắn JSON ping
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"type\": \"ping\",");
        jsonBuilder.append("\"timestamp\": ").append(new Date().getTime());
        jsonBuilder.append("}");
        
        sendMessage(jsonBuilder.toString());
    }
    
    // Implementations of WebSocketCallback interface
    
    @Override
    public void onOpen() {
        isConnected = true;
        GWT.log("WebSocket đã kết nối");
        
        // Thông báo cho tất cả các listeners
        for (WebSocketListener listener : listeners) {
            listener.onConnected();
        }
        
        // Đăng ký tên người dùng nếu đã được đặt
        if (currentUserName != null && !currentUserName.isEmpty()) {
            registerUserName(currentUserName);
        }
    }
    
    @Override
    public void onClose(int code, String reason) {
        isConnected = false;
        GWT.log("WebSocket đã đóng với mã: " + code + ", lý do: " + reason);
        
        // Thông báo cho tất cả các listeners
        for (WebSocketListener listener : listeners) {
            listener.onDisconnected(code, reason);
        }
    }
    
    @Override
    public void onMessage(String message) {
        GWT.log("Nhận được tin nhắn: " + message);
        
        // Thông báo cho tất cả các listeners
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
        String searchPattern = "\"" + fieldName + "\"\\s*:\\s*\"";
        int startIndex = jsonString.indexOf(searchPattern);
        if (startIndex < 0) {
            // Kiểm tra trường hợp giá trị không phải là chuỗi (không có dấu ngoặc kép)
            searchPattern = "\"" + fieldName + "\"\\s*:\\s*";
            startIndex = jsonString.indexOf(searchPattern);
            if (startIndex < 0) {
                return null;
            }
            startIndex += searchPattern.length();
            
            // Tìm dấu phẩy hoặc dấu ngoặc đóng
            int endIndex = jsonString.indexOf(",", startIndex);
            if (endIndex < 0) {
                endIndex = jsonString.indexOf("}", startIndex);
            }
            if (endIndex < 0) {
                return jsonString.substring(startIndex);
            }
            return jsonString.substring(startIndex, endIndex).trim();
        } else {
            startIndex += searchPattern.length();
            int endIndex = jsonString.indexOf("\"", startIndex);
            return endIndex < 0 ? null : jsonString.substring(startIndex, endIndex);
        }
    }
    
    /**
     * Escape ký tự đặc biệt trong chuỗi JSON
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '/':
                    escaped.append("\\/");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
