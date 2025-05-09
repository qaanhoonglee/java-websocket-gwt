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
    private static final String USER_NAME_LABEL = "Tên người dùng:";
    private static final String RECIPIENT_LABEL = "Gửi đến:";
    
    // Message constants
    private static final int MESSAGE_TIME_COLUMN = 0;
    private static final int MESSAGE_TEXT_COLUMN = 1;
    private static final int MESSAGE_TABLE_COLUMNS = 2;
    
    // WebSocket object
    private WebSocket webSocket;
    private String currentUserName = "";
    
    // UI Components
    private DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
    private VerticalPanel contentPanel = new VerticalPanel();
    private FlexTable messagesTable = new FlexTable();
    private ScrollPanel scrollPanel = new ScrollPanel(messagesTable);
    private TextBox messageInput = new TextBox();
    private TextBox userNameInput = new TextBox();
    private TextBox recipientInput = new TextBox();
    private Button sendButton = new Button(SEND_BUTTON_TEXT);
    private Button connectButton = new Button(CONNECT_BUTTON_TEXT);
    private Button disconnectButton = new Button(DISCONNECT_BUTTON_TEXT);
    private Button clearButton = new Button(CLEAR_BUTTON_TEXT);
    private Label statusLabel = new Label(STATUS_DISCONNECTED);
    private DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm:ss");/**
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
     */    private void setupUI() {
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
        
        // Tạo panel thông tin kết nối (userName + trạng thái)
        HorizontalPanel connectionPanel = new HorizontalPanel();
        connectionPanel.setWidth("100%");
        connectionPanel.setSpacing(10);
        connectionPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        
        // Panel nhập tên người dùng
        HorizontalPanel userNamePanel = new HorizontalPanel();
        userNamePanel.setSpacing(5);
        userNamePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        
        Label userNameLabel = new Label(USER_NAME_LABEL);
        userNameInput.setWidth("150px");
        
        userNamePanel.add(userNameLabel);
        userNamePanel.add(userNameInput);
        connectionPanel.add(userNamePanel);
        
        // Panel trạng thái
        HorizontalPanel statusPanel = new HorizontalPanel();
        statusPanel.setWidth("100%");
        statusPanel.setStyleName("gwt-status-panel");
        
        statusLabel.setStyleName("gwt-status-label");
        statusPanel.add(statusLabel);
        connectionPanel.add(statusPanel);
        connectionPanel.setCellHorizontalAlignment(statusPanel, HasHorizontalAlignment.ALIGN_RIGHT);
        
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
        
        // Panel nhập người nhận
        HorizontalPanel recipientPanel = new HorizontalPanel();
        recipientPanel.setWidth("100%");
        recipientPanel.setSpacing(5);
        recipientPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        
        Label recipientLabel = new Label(RECIPIENT_LABEL);
        recipientInput.setWidth("150px");
        recipientInput.setTitle("Để trống để gửi cho tất cả mọi người");
        
        recipientPanel.add(recipientLabel);
        recipientPanel.add(recipientInput);
        
        // Tạo panel nhập nội dung tin nhắn
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
        contentPanel.add(connectionPanel);
        contentPanel.add(messagesDecoratorPanel);
        contentPanel.add(recipientPanel);
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
     */    private void connectWebSocket() {
        try {
            // Kiểm tra nếu tên người dùng trống
            currentUserName = userNameInput.getText().trim();
            if (currentUserName.isEmpty()) {
                addMessage("Lỗi", "Vui lòng nhập tên người dùng trước khi kết nối");
                return;
            }
            
            // Đóng kết nối cũ nếu có
            if (webSocket != null) {
                webSocket.close();
            }
            
            // Lấy hostname từ URL hiện tại và tạo URL WebSocket
            String host = Window.Location.getHost();
            String wsUrl = "ws://" + host + "/websocket-server/notification-ws";
            
            addMessage("Hệ thống", "Đang kết nối tới: " + wsUrl + " với tên người dùng: " + currentUserName);
            
            // Tạo kết nối WebSocket mới
            webSocket = WebSocket.create(wsUrl);
            
            // Thiết lập các callback cho WebSocket
            WebSocketCallback callback = new WebSocketCallback() {                @Override
                public void onMessage(String message) {
                    try {
                        // Thử phân tích tin nhắn dạng JSON
                        if (message.startsWith("{")) {
                            // Giả lập xử lý JSON bằng cách tìm các trường
                            if (message.contains("\"type\"")) {
                                String type = extractJsonField(message, "type");
                                
                                if ("chat".equals(type)) {
                                    String sender = extractJsonField(message, "sender");
                                    String content = extractJsonField(message, "content");
                                    addMessage(sender, content);
                                    return;
                                } else if ("private-chat".equals(type)) {
                                    String sender = extractJsonField(message, "sender");
                                    String content = extractJsonField(message, "content");
                                    addMessage("[Tin nhắn riêng từ " + sender + "]", content);
                                    return;
                                } else if ("private-chat-sent".equals(type)) {
                                    String recipient = extractJsonField(message, "recipient");
                                    String content = extractJsonField(message, "content");
                                    addMessage("[Đã gửi riêng cho " + recipient + "]", content);
                                    return;
                                } else if ("system".equals(type)) {
                                    String systemMsg = extractJsonField(message, "message");
                                    addMessage("Hệ thống", systemMsg);
                                    return;
                                } else if ("error".equals(type)) {
                                    String errorMsg = extractJsonField(message, "message");
                                    addMessage("Lỗi", errorMsg);
                                    return;
                                } else if ("welcome".equals(type)) {
                                    String welcomeMsg = extractJsonField(message, "message");
                                    addMessage("Server", welcomeMsg);
                                    return;
                                } else if ("register-confirm".equals(type)) {
                                    String userName = extractJsonField(message, "userName");
                                    addMessage("Hệ thống", "Đã đăng ký thành công với tên: " + userName);
                                    return;
                                }
                            }
                        }
                        
                        // Nếu không phải JSON hoặc không xử lý được, hiển thị nguyên văn
                        addMessage("Server", message);
                    } catch (Exception e) {
                        addMessage("Server", message);
                    }
                }

                @Override
                public void onOpen() {
                    statusLabel.setText(STATUS_CONNECTED);
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                    sendButton.setEnabled(true);
                    addMessage("Hệ thống", "WebSocket đã kết nối");
                    
                    // Đăng ký tên người dùng với server
                    String registerMessage = "{\"type\":\"register\",\"userName\":\"" + currentUserName + "\"}";
                    webSocket.send(registerMessage);
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
    }    /**
     * Ngắt kết nối WebSocket
     */
    private void disconnectWebSocket() {
        if (webSocket != null) {
            webSocket.close();
            webSocket = null;
            currentUserName = "";
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
            String recipient = recipientInput.getText().trim();
            String jsonMessage;
            
            if (recipient.isEmpty()) {
                // Gửi tin nhắn cho tất cả
                jsonMessage = "{\"type\":\"chat\",\"content\":\"" + message + "\"}";
                addMessage("Bạn (cho tất cả)", message);
            } else {
                // Gửi tin nhắn cho người dùng cụ thể
                jsonMessage = "{\"type\":\"chat\",\"content\":\"" + message + "\",\"recipient\":\"" + recipient + "\"}";
                addMessage("Bạn (riêng cho " + recipient + ")", message);
            }
            
            webSocket.send(jsonMessage);
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
      /**
     * Phương thức đơn giản để trích xuất giá trị từ một trường trong chuỗi JSON
     * Lưu ý: Đây là một cách thực hiện đơn giản, không phải là một JSON parser đầy đủ
     */
    private String extractJsonField(String jsonString, String fieldName) {
        // Dùng phân tích đơn giản để tìm giá trị
        // Lưu ý: chỉ áp dụng cho JSON đơn giản, không xử lý các trường hợp phức tạp
        String value = null;
        
        try {
            // Có thể tìm theo từ khóa trước rồi phân tích đơn giản
            int startPos = jsonString.indexOf("\"" + fieldName + "\"");
            if (startPos >= 0) {
                // Tìm dấu :
                int colonPos = jsonString.indexOf(":", startPos);
                if (colonPos >= 0) {
                    // Tìm vị trí bắt đầu và kết thúc của giá trị
                    int valueStart = colonPos + 1;
                    // Bỏ qua khoảng trắng sau dấu :
                    while (valueStart < jsonString.length() && 
                           (jsonString.charAt(valueStart) == ' ' || 
                            jsonString.charAt(valueStart) == '\t')) {
                        valueStart++;
                    }
                    
                    if (valueStart < jsonString.length()) {
                        char startChar = jsonString.charAt(valueStart);
                        if (startChar == '"') {
                            // Giá trị là chuỗi
                            int valueEnd = jsonString.indexOf("\"", valueStart + 1);
                            if (valueEnd >= 0) {
                                value = jsonString.substring(valueStart + 1, valueEnd);
                            }
                        } else if (startChar == '{' || startChar == '[') {
                            // Giá trị là đối tượng hoặc mảng - không xử lý
                            value = "[object]";
                        } else {
                            // Giá trị là số hoặc boolean
                            int valueEnd = jsonString.indexOf(",", valueStart);
                            if (valueEnd < 0) {
                                valueEnd = jsonString.indexOf("}", valueStart);
                            }
                            if (valueEnd >= 0) {
                                value = jsonString.substring(valueStart, valueEnd).trim();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            GWT.log("Lỗi khi phân tích JSON: " + e.getMessage(), e);
        }
        
        return value;
    }
}