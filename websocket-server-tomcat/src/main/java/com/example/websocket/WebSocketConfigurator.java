package com.example.websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Lớp này sẽ được gọi khi ứng dụng khởi động để đảm bảo 
 * WebSocket endpoints được đăng ký
 */
@WebListener
public class WebSocketConfigurator implements ServletContextListener {    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== WebSocketConfigurator: Khởi tạo WebSocket ===");
        // Lấy container của WebSocket từ ServletContext
        ServerContainer serverContainer = (ServerContainer) sce.getServletContext()
                .getAttribute("javax.websocket.server.ServerContainer");
        
        if (serverContainer == null) {
            System.err.println("Không thể lấy ServerContainer. WebSocket sẽ không hoạt động!");
            return;
        }
        
        try {
            // Đăng ký endpoint theo cách thủ công nếu cần
            // Không cần thiết khi sử dụng @ServerEndpoint, nhưng đảm bảo endpoint luôn được đăng ký
            ServerEndpointConfig config = ServerEndpointConfig.Builder
                    .create(WebSocketEndpoint.class, "/notification-ws")
                    .build();
            
            serverContainer.addEndpoint(config);
            
            // Hoặc có thể đăng ký endpoint được annotated một cách đơn giản
            // serverContainer.addEndpoint(WebSocketEndpoint.class);
            
            System.out.println("=== WebSocket endpoint đã được khởi tạo thành công ===");
            
            // Khởi động dịch vụ gửi tin nhắn định kỳ
            MessageSender.getInstance();
            System.out.println("=== Khởi động dịch vụ gửi tin nhắn định kỳ ===");
            
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo WebSocket endpoint: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== WebSocketConfigurator: Dọn dẹp WebSocket ===");
        
        // Dừng dịch vụ gửi tin nhắn định kỳ
        try {
            MessageSender.getInstance().shutdown();
            System.out.println("=== Đã dừng dịch vụ gửi tin nhắn định kỳ ===");
        } catch (Exception e) {
            System.err.println("Lỗi khi dừng MessageSender: " + e.getMessage());
            e.printStackTrace();
        }
    }
}