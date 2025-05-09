package com.example.websocket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Tiện ích để gửi tin nhắn định kỳ cho tất cả client đã kết nối
 * Ví dụ cho việc push notification từ server
 */
public class MessageSender {
    
    private final SocketHandler socketHandler;
    private final ScheduledExecutorService scheduler;
    private static final MessageSender INSTANCE = new MessageSender();
    
    private MessageSender() {
        this.socketHandler = SocketHandler.getInstance();
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        // Khởi động việc gửi tin nhắn định kỳ
        startSendingMessages();
    }
    
    public static MessageSender getInstance() {
        return INSTANCE;
    }
      private void startSendingMessages() {
        scheduler.scheduleAtFixedRate(() -> {
            // Sử dụng JSON format cho tất cả các tin nhắn để duy trì tính nhất quán
            String message = javax.json.Json.createObjectBuilder()
                .add("type", "server-time")
                .add("timestamp", System.currentTimeMillis())
                .add("serverTime", java.time.LocalDateTime.now().toString())
                .build().toString();
            socketHandler.broadcastMessage(message);
        }, 2, 10, TimeUnit.MINUTES);
    }
    
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(25, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
