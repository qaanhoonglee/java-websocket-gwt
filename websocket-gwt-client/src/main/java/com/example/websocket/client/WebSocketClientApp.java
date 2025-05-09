package com.example.websocket.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.Date;

/**
 * Entry point cho ứng dụng GWT WebSocket Client
 */
public class WebSocketClientApp implements EntryPoint {    // Constants
    private static final String HEADER_TITLE = "GWT WebSocket Client";
    private static final String SEND_BUTTON_TEXT = "Gửi";
    private static final String CONNECT_BUTTON_TEXT = "Kết nối";
    private static final String DISCONNECT_BUTTON_TEXT = "Ngắt kết nối";
    private static final String CLEAR_BUTTON_TEXT = "Xóa tin nhắn";
    private static final String STATUS_DISCONNECTED = "Trạng thái: Chưa kết nối";
    private static final String STATUS_CONNECTED = "Trạng thái: Đã kết nối";
    private static final String STATUS_ERROR = "Trạng thái: Lỗi kết nối";
    
    // Message constants
    private static final int MESSAGE_TIME_COLUMN = 0;
    private static final int MESSAGE_TEXT_COLUMN = 1;
    private static final int MESSAGE_TABLE_COLUMNS = 2;
    
    // WebSocket object
    private WebSocket webSocket;
    
    // UI Components
    private DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
    private VerticalPanel contentPanel = new VerticalPanel();
    private FlexTable messagesTable = new FlexTable();
    private ScrollPanel scrollPanel = new ScrollPanel(messagesTable);
    private TextBox messageInput = new TextBox();
    private Button sendButton = new Button(SEND_BUTTON_TEXT);
    private Button connectButton = new Button(CONNECT_BUTTON_TEXT);
    private Button disconnectButton = new Button(DISCONNECT_BUTTON_TEXT);
    private Button clearButton = new Button(CLEAR_BUTTON_TEXT);
    private Label statusLabel = new Label(STATUS_DISCONNECTED);
    private DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm:ss");    /**
     * Entry point method - được gọi khi ứng dụng GWT khởi động
     */
    public void onModuleLoad() {
        // Khởi tạo các thành phần UI
        setupUI();
        
        // Thiết lập xử lý sự kiện
        setupEventHandlers();
        
        // Thêm UI vào trang
        RootLayoutPanel.get().add(mainPanel);
    }
      /**
     * Thiết lập giao diện người dùng
     */
    private void setupUI() {
        mainPanel.setSize("100%", "100%");
        
        // Tạo panel chứa nội dung 
        contentPanel.setSpacing(10);
        contentPanel.setWidth("100%");
        
        // Tạo panel tiêu đề
        HorizontalPanel headerPanel = new HorizontalPanel();
        headerPanel.setWidth("100%");
        headerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        
        HTML headerLabel = new HTML("<h2>" + HEADER_TITLE + "</h2>");
        headerPanel.add(headerLabel);
        
        // Tạo panel trạng thái kết nối
        HorizontalPanel statusPanel = new HorizontalPanel();
        statusPanel.setWidth("100%");
        statusPanel.setStyleName("gwt-status-panel");
        
        statusLabel.setStyleName("gwt-status-label");
        statusPanel.add(statusLabel);
        
        // Panel hiển thị tin nhắn
        messagesTable.setWidth("100%");
        messagesTable.setCellPadding(5);
        messagesTable.setCellSpacing(0);
        messagesTable.addStyleName("gwt-messages-table");
        messagesTable.getColumnFormatter().setWidth(MESSAGE_TIME_COLUMN, "100px");
        
        scrollPanel.setHeight("300px");
        scrollPanel.setWidth("100%");
        scrollPanel.addStyleName("gwt-messages-scroll");
        
        // Tạo decorator panel cho tin nhắn để thêm viền
        DecoratorPanel messagesDecoratorPanel = new DecoratorPanel();
        messagesDecoratorPanel.setWidth("100%");
        messagesDecoratorPanel.add(scrollPanel);
        
        // Tạo panel nhập liệu
        HorizontalPanel inputPanel = new HorizontalPanel();
        inputPanel.setWidth("100%");
        inputPanel.setSpacing(5);
        inputPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        
        messageInput.setWidth("100%");
        messageInput.addStyleName("gwt-message-input");
        
        sendButton.setEnabled(false);
        sendButton.addStyleName("gwt-send-button");
        
        inputPanel.add(messageInput);
        inputPanel.setCellWidth(messageInput, "85%");
        inputPanel.add(sendButton);
        
        // Panel điều khiển kết nối
        HorizontalPanel controlPanel = new HorizontalPanel();
        controlPanel.setSpacing(10);
        controlPanel.setWidth("100%");
        controlPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        
        disconnectButton.setEnabled(false);
        
        controlPanel.add(connectButton);
        controlPanel.add(disconnectButton);
        controlPanel.add(clearButton);
        
        // Thêm các thành phần vào content panel
        contentPanel.add(headerPanel);
        contentPanel.add(statusPanel);
        contentPanel.add(messagesDecoratorPanel);
        contentPanel.add(inputPanel);
        contentPanel.add(controlPanel);
        
        // Đặt content panel vào center của main panel
        mainPanel.add(contentPanel);
    }
      /**
     * Thiết lập các xử lý sự kiện
     */
    private void setupEventHandlers() {
        // Sự kiện gửi tin nhắn
        sendButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                sendMessage();
            }
        });
        
        // Sự kiện nhấn phím Enter để gửi tin nhắn
        messageInput.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendMessage();
                }
            }
        });
        
        // Sự kiện kết nối WebSocket
        connectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                connectWebSocket();
            }
        });
        
        // Sự kiện ngắt kết nối WebSocket
        disconnectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                disconnectWebSocket();
            }
        });
        
        // Sự kiện xóa tin nhắn
        clearButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clearMessages();
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
            
            addMessage("Hệ thống", "Đang kết nối tới: " + wsUrl);
            
            // Tạo kết nối WebSocket mới
            webSocket = WebSocket.create(wsUrl);
            
            // Thiết lập các callback cho WebSocket
            WebSocketCallback callback = new WebSocketCallback() {
                @Override
                public void onMessage(String message) {
                    addMessage("Server", message);
                }

                @Override
                public void onOpen() {
                    statusLabel.setText(STATUS_CONNECTED);
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                    sendButton.setEnabled(true);
                    addMessage("Hệ thống", "WebSocket đã kết nối");
                }

                @Override
                public void onClose(int code, String reason) {
                    statusLabel.setText(STATUS_DISCONNECTED + " (Mã: " + code + ")");
                    connectButton.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    sendButton.setEnabled(false);
                    addMessage("Hệ thống", "WebSocket đã đóng. Mã: " + code + ", Lý do: " + reason);
                }

                @Override
                public void onError() {
                    statusLabel.setText(STATUS_ERROR);
                    connectButton.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    sendButton.setEnabled(false);
                    addMessage("Hệ thống", "Xảy ra lỗi WebSocket");
                }
            };
            
            webSocket.setOnmessage(callback);
            webSocket.setOnopen(callback);
            webSocket.setOnclose(callback);
            webSocket.setOnerror(callback);
        } catch (Exception e) {
            addMessage("Lỗi", "Lỗi khi kết nối: " + e.getMessage());
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
            addMessage("Hệ thống", "Đã đóng kết nối WebSocket");
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            sendButton.setEnabled(false);
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
            addMessage("Lỗi", "WebSocket chưa kết nối!");
            return;
        }
        
        try {
            webSocket.send(message);
            addMessage("Bạn", message);
            messageInput.setText("");
            messageInput.setFocus(true);
        } catch (Exception e) {
            addMessage("Lỗi", "Lỗi khi gửi tin nhắn: " + e.getMessage());
            GWT.log("Lỗi khi gửi tin nhắn", e);
        }
    }
    
    /**
     * Xóa tất cả tin nhắn
     */
    private void clearMessages() {
        messagesTable.removeAllRows();
    }
    
    /**
     * Thêm tin nhắn vào bảng hiển thị
     * 
     * @param sender Người gửi tin nhắn
     * @param text Nội dung tin nhắn
     */
    private void addMessage(String sender, String text) {
        // Lấy thời gian hiện tại
        String time = timeFormat.format(new Date());
        
        // Lấy số hàng hiện tại trong bảng
        int row = messagesTable.getRowCount();
        
        // Thêm thời gian
        Label timeLabel = new Label(time);
        timeLabel.addStyleName("gwt-message-time");
        messagesTable.setWidget(row, MESSAGE_TIME_COLUMN, timeLabel);
        
        // Thêm nội dung tin nhắn
        SafeHtmlBuilder messageBuilder = new SafeHtmlBuilder();
        messageBuilder.appendEscaped(sender + ": " + text);
        
        HTML messageHTML = new HTML(messageBuilder.toSafeHtml());
        messageHTML.addStyleName("gwt-message-content");
        messagesTable.setWidget(row, MESSAGE_TEXT_COLUMN, messageHTML);
        
        // Thiết lập style cho hàng tin nhắn
        if (row % 2 == 0) {
            messagesTable.getRowFormatter().addStyleName(row, "gwt-message-row-even");
        } else {
            messagesTable.getRowFormatter().addStyleName(row, "gwt-message-row-odd");
        }
        
        // Tự động cuộn xuống tin nhắn mới nhất
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                scrollPanel.scrollToBottom();
            }
        });
    }
}