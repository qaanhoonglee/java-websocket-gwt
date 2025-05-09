package com.example.websocket.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

/**
 * Entry point cho ứng dụng GWT WebSocket Client
 */
public class WebSocketClientApp implements EntryPoint {

    private WebSocket webSocket;
    private VerticalPanel mainPanel = new VerticalPanel();
    private FlowPanel messagesPanel = new FlowPanel();
    private TextBox messageInput = new TextBox();
    private Button sendButton = new Button("Gửi");
    private Button connectButton = new Button("Kết nối");
    private Button disconnectButton = new Button("Ngắt kết nối");
    private Label statusLabel = new Label("Trạng thái: Chưa kết nối");

    /**
     * Entry point method - được gọi khi ứng dụng GWT khởi động
     */
    public void onModuleLoad() {
        // Thiết lập giao diện
        setupUI();
        
        // Thiết lập xử lý sự kiện
        setupEventHandlers();
        
        // Thêm UI vào trang
        RootPanel.get().add(mainPanel);
    }
    
    /**
     * Thiết lập giao diện người dùng
     */
    private void setupUI() {
        mainPanel.setWidth("100%");
        mainPanel.addStyleName("main-panel");
        
        // Panel hiển thị trạng thái
        FlowPanel statusPanel = new FlowPanel();
        statusPanel.addStyleName("status-panel");
        statusPanel.add(statusLabel);
        
        // Panel chứa tin nhắn
        messagesPanel.addStyleName("messages-panel");
        messagesPanel.setHeight("300px");
        messagesPanel.getElement().getStyle().setProperty("overflow", "auto");
        messagesPanel.getElement().getStyle().setProperty("border", "1px solid #ccc");
        messagesPanel.getElement().getStyle().setProperty("padding", "10px");
        messagesPanel.getElement().getStyle().setProperty("marginBottom", "10px");
        
        // Panel nhập tin nhắn và gửi
        FlowPanel inputPanel = new FlowPanel();
        inputPanel.addStyleName("input-panel");
        
        messageInput.setWidth("70%");
        messageInput.getElement().getStyle().setProperty("marginRight", "10px");
        messageInput.getElement().getStyle().setProperty("padding", "5px");
        
        inputPanel.add(messageInput);
        inputPanel.add(sendButton);
        
        // Panel chứa nút kết nối/ngắt kết nối
        FlowPanel connectionPanel = new FlowPanel();
        connectionPanel.addStyleName("connection-panel");
        connectionPanel.getElement().getStyle().setProperty("marginTop", "10px");
        
        connectionPanel.add(connectButton);
        connectionPanel.add(new HTML("&nbsp;"));
        connectionPanel.add(disconnectButton);
        
        // Thêm tất cả các panel vào panel chính
        mainPanel.add(new HTML("<h2>GWT WebSocket Client</h2>"));
        mainPanel.add(statusPanel);
        mainPanel.add(messagesPanel);
        mainPanel.add(inputPanel);
        mainPanel.add(connectionPanel);
    }
    
    /**
     * Thiết lập các xử lý sự kiện
     */
    private void setupEventHandlers() {
        // Xử lý sự kiện gửi tin nhắn khi click nút Gửi
        sendButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                sendMessage();
            }
        });
        
        // Xử lý sự kiện gửi tin nhắn khi nhấn phím Enter
        messageInput.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendMessage();
                }
            }
        });
        
        // Xử lý sự kiện kết nối WebSocket
        connectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                connectWebSocket();
            }
        });
        
        // Xử lý sự kiện ngắt kết nối WebSocket
        disconnectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                disconnectWebSocket();
            }
        });
    }
    
    /**
     * Kết nối tới WebSocket server
     */
    private void connectWebSocket() {
        try {
            // Đóng kết nối cũ nếu có
            if (webSocket != null) {
                webSocket.close();
            }
            
            // Lấy hostname từ URL hiện tại và tạo URL WebSocket
            String host = Window.Location.getHost();
            String wsUrl = "ws://" + host + "/websocket-server/notification-ws";
            
            addMessage("Đang kết nối tới: " + wsUrl);
            
            // Tạo kết nối WebSocket mới
            webSocket = WebSocket.create(wsUrl);
            
            // Thiết lập các callback cho WebSocket
            webSocket.setOnmessage(new WebSocketCallback() {
                @Override
                public void onMessage(String message) {
                    addMessage("Nhận: " + message);
                }

                @Override
                public void onOpen() {
                    statusLabel.setText("Trạng thái: Đã kết nối");
                    addMessage("WebSocket đã kết nối");
                }

                @Override
                public void onClose(int code, String reason) {
                    statusLabel.setText("Trạng thái: Đã ngắt kết nối (Mã: " + code + ")");
                    addMessage("WebSocket đã đóng. Mã: " + code + ", Lý do: " + reason);
                }

                @Override
                public void onError() {
                    statusLabel.setText("Trạng thái: Lỗi kết nối");
                    addMessage("Xảy ra lỗi WebSocket");
                }
            });
        } catch (Exception e) {
            addMessage("Lỗi khi kết nối: " + e.getMessage());
            GWT.log("Lỗi khi kết nối WebSocket", e);
        }
    }
    
    /**
     * Ngắt kết nối WebSocket
     */
    private void disconnectWebSocket() {
        if (webSocket != null) {
            webSocket.close();
            webSocket = null;
            addMessage("Đã đóng kết nối WebSocket");
        }
    }
    
    /**
     * Gửi tin nhắn qua WebSocket
     */
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        
        if (webSocket == null || webSocket.getReadyState() != WebSocket.OPEN) {
            addMessage("Lỗi: WebSocket chưa kết nối!");
            return;
        }
        
        try {
            webSocket.send(message);
            addMessage("Đã gửi: " + message);
            messageInput.setText("");
            messageInput.setFocus(true);
        } catch (Exception e) {
            addMessage("Lỗi khi gửi tin nhắn: " + e.getMessage());
            GWT.log("Lỗi khi gửi tin nhắn", e);
        }
    }
    
    /**
     * Thêm tin nhắn vào panel hiển thị
     */
    private void addMessage(String text) {
        Label messageLabel = new Label(text);
        messagesPanel.add(messageLabel);
        
        // Tự động cuộn xuống phía dưới để thấy tin nhắn mới nhất
        messagesPanel.getElement().setScrollTop(messagesPanel.getElement().getScrollHeight());
    }
}