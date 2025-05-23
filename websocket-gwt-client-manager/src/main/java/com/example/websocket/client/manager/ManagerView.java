package com.example.websocket.client.manager;

import com.example.websocket.core.WebSocketListener;
import com.example.websocket.core.WebSocketManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.Date;

/**
 * View hiển thị giao diện quản lý và xử lý tương tác người dùng
 * Đã đơn giản hóa để chỉ chứa các sự kiện gửi và nhận message
 */
public class ManagerView extends Composite implements WebSocketListener {

    // Constants
    private static final String HEADER_TITLE = "GWT WebSocket Client Manager";
    private static final String SEND_BUTTON_TEXT = "Gửi";
    private static final String CLEAR_BUTTON_TEXT = "Xóa tin nhắn";
    private static final String STATUS_DISCONNECTED = "Trạng thái : Chưa kết nối";
    private static final String STATUS_CONNECTED = "Trạng thái Manage connect: Đã kết nối";
    private static final String STATUS_ERROR = "Trạng thái: Lỗi kết nối";

    // Message constants
    private static final int MESSAGE_TIME_COLUMN = 0;
    private static final int MESSAGE_TEXT_COLUMN = 1;

    // WebSocketManager
    private WebSocketManager webSocketManager;

    // UI Components
    private DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
    private VerticalPanel contentPanel = new VerticalPanel();
    private FlexTable messagesTable = new FlexTable();
    private ScrollPanel scrollPanel = new ScrollPanel(messagesTable);
    private TextBox messageInput = new TextBox();
    private Button sendButton = new Button(SEND_BUTTON_TEXT);
    private Button clearButton = new Button(CLEAR_BUTTON_TEXT);
    private Label statusLabel = new Label(STATUS_DISCONNECTED);
    private DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm:ss");

    /**
     * Constructor
     */
    public ManagerView() {
        // Khởi tạo WebSocketManager
        webSocketManager = WebSocketManager.getInstance();

        // Đăng ký với WebSocketManager để nhận các sự kiện
        webSocketManager.addListener(this);

        // Khởi tạo UI
        initWidget(mainPanel);
        setupUI();
        setupEventHandlers();

        // Cập nhật trạng thái
        updateConnectionStatus();
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

        // Tạo panel trạng thái
        HorizontalPanel statusPanel = new HorizontalPanel();
        statusPanel.setSpacing(10);
        statusPanel.add(statusLabel);

        // Tạo bảng tin nhắn
        messagesTable.setCellPadding(5);
        messagesTable.setCellSpacing(0);
        messagesTable.setWidth("100%");

        scrollPanel.setHeight("300px");
        scrollPanel.setWidth("100%");

        // Tạo panel nhập tin nhắn
        HorizontalPanel inputPanel = new HorizontalPanel();
        inputPanel.setSpacing(5);
        inputPanel.setWidth("100%");

        messageInput.setWidth("100%");
        inputPanel.add(messageInput);
        inputPanel.add(sendButton);
        inputPanel.add(clearButton);

        inputPanel.setCellWidth(messageInput, "100%");

        // Thêm các components vào panel chính
        contentPanel.add(headerPanel);
        contentPanel.add(statusPanel);
        contentPanel.add(scrollPanel);
        contentPanel.add(inputPanel);

        mainPanel.add(contentPanel);
    }

    /**
     * Thiết lập các xử lý sự kiện
     */
    private void setupEventHandlers() {
        // Xử lý sự kiện khi nhấn nút gửi
        sendButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                sendMessage();
            }
        });

        // Xử lý sự kiện khi nhấn Enter trong input
        messageInput.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendMessage();
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút xóa tin nhắn
        clearButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clearMessages();
            }
        });
    }

    /**
     * Cập nhật trạng thái kết nối hiển thị
     */
    private void updateConnectionStatus() {
        if (webSocketManager.isConnected()) {
            statusLabel.setText(STATUS_CONNECTED);
        } else {
            statusLabel.setText(STATUS_DISCONNECTED);
        }
    }

    /**
     * Gửi tin nhắn
     */
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && webSocketManager.isConnected()) {
            webSocketManager.sendChatMessage(message, null);
            messageInput.setText("");
            messageInput.setFocus(true);
        }
    }

    /**
     * Xóa tin nhắn
     */
    private void clearMessages() {
        messagesTable.removeAllRows();
    }

    /**
     * Hiển thị tin nhắn trong bảng
     */
    private void addMessageToTable(String time, String message, boolean isSystemMessage) {
        int row = messagesTable.getRowCount();

        messagesTable.setText(row, MESSAGE_TIME_COLUMN, time);

        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        safeHtmlBuilder.appendEscaped(message);
        HTML messageHtml = new HTML(safeHtmlBuilder.toSafeHtml());

        messagesTable.setWidget(row, MESSAGE_TEXT_COLUMN, messageHtml);

        // CSS cho các cột
        messagesTable.getCellFormatter().addStyleName(row, MESSAGE_TIME_COLUMN, "gwt-message-time");
        messagesTable.getCellFormatter().addStyleName(row, MESSAGE_TEXT_COLUMN, "gwt-message-content");

        // CSS cho hàng chẵn/lẻ
        if (row % 2 == 0) {
            messagesTable.getRowFormatter().addStyleName(row, "gwt-message-row-even");
        }

        // CSS cho thông báo hệ thống
        if (isSystemMessage) {
            messagesTable.getRowFormatter().addStyleName(row, "gwt-message-system");
        }

        // Cuộn xuống tin nhắn mới nhất
        scrollPanel.scrollToBottom();
    }

    // WebSocketListener interface implementations

    @Override
    public void onConnected() {
        updateConnectionStatus();
        String time = timeFormat.format(new Date());
        addMessageToTable(time, "Đã kết nối tới server", true);
    }

    @Override
    public void onDisconnected(int code, String reason) {
        updateConnectionStatus();
        String time = timeFormat.format(new Date());
        addMessageToTable(time, "Đã ngắt kết nối: " + reason, true);
    }

    @Override
    public void onMessageReceived(String message) {
        String time = timeFormat.format(new Date());

        // Parse thông tin từ tin nhắn JSON
        String type = WebSocketManager.extractJsonField(message, "type");

        if ("chat".equals(type)) {
            String sender = WebSocketManager.extractJsonField(message, "sender");
            String content = WebSocketManager.extractJsonField(message, "content");

            if (content != null) {
                // Hiển thị tin nhắn từ người gửi
                addMessageToTable(time, sender + ": " + content, false);
            }
        } else if ("system".equals(type)) {
            String content = WebSocketManager.extractJsonField(message, "content");
            if (content != null) {
                // Hiển thị tin nhắn hệ thống
                addMessageToTable(time, "Hệ thống: " + content, true);
            }
        }
    }

    @Override
    public void onError() {
        statusLabel.setText(STATUS_ERROR);
        String time = timeFormat.format(new Date());
        addMessageToTable(time, "Lỗi kết nối WebSocket", true);
    }
}
