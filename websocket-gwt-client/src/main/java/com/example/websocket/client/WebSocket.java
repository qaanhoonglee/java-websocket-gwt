package com.example.websocket.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Lớp WebSocket - Wrapper cho JavaScript WebSocket API
 * Cho phép sử dụng WebSocket trình duyệt từ ứng dụng GWT
 */
public class WebSocket extends JavaScriptObject {
    
    // Các trạng thái WebSocket theo tiêu chuẩn
    public static final int CONNECTING = 0;
    public static final int OPEN = 1;
    public static final int CLOSING = 2;
    public static final int CLOSED = 3;
    
    // Constructor bảo vệ theo yêu cầu của GWT
    protected WebSocket() {}
    
    /**
     * Tạo kết nối WebSocket mới tới URL chỉ định
     */
    public static native WebSocket create(String url) /*-{
        return new $wnd.WebSocket(url);
    }-*/;
    
    /**
     * Trả về trạng thái hiện tại của kết nối WebSocket
     */
    public final native int getReadyState() /*-{
        return this.readyState;
    }-*/;
    
    /**
     * Gửi dữ liệu thông qua kết nối WebSocket
     */
    public final native void send(String data) /*-{
        this.send(data);
    }-*/;
    
    /**
     * Đóng kết nối WebSocket
     */
    public final native void close() /*-{
        this.close();
    }-*/;
    
    /**
     * Đăng ký callback cho sự kiện message (khi nhận được tin nhắn từ server)
     */
    public final native void setOnmessage(WebSocketCallback callback) /*-{
        this.onmessage = function(e) {
            callback.@com.example.websocket.client.WebSocketCallback::onMessage(Ljava/lang/String;)(e.data);
        };
    }-*/;
    
    /**
     * Đăng ký callback cho sự kiện open (khi kết nối được thiết lập)
     */
    public final native void setOnopen(WebSocketCallback callback) /*-{
        this.onopen = function(e) {
            callback.@com.example.websocket.client.WebSocketCallback::onOpen()();
        };
    }-*/;
    
    /**
     * Đăng ký callback cho sự kiện close (khi kết nối đóng)
     */
    public final native void setOnclose(WebSocketCallback callback) /*-{
        this.onclose = function(e) {
            callback.@com.example.websocket.client.WebSocketCallback::onClose(ILjava/lang/String;)(e.code, e.reason);
        };
    }-*/;
    
    /**
     * Đăng ký callback cho sự kiện error (khi có lỗi xảy ra)
     */
    public final native void setOnerror(WebSocketCallback callback) /*-{
        this.onerror = function(e) {
            callback.@com.example.websocket.client.WebSocketCallback::onError()();
        };
    }-*/;
}