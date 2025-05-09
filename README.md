# Java WebSocket với GWT

Dự án mẫu kết hợp WebSocket và Google Web Toolkit (GWT) để xây dựng ứng dụng thời gian thực.

## Tổng quan

Dự án này bao gồm hai thành phần chính:
- **Server WebSocket** (websocket-server-tomcat): Cung cấp endpoint WebSocket để xử lý kết nối và tin nhắn
- **Client GWT** (websocket-gwt-client): Cung cấp giao diện người dùng sử dụng GWT 2.7

## Yêu cầu

- JDK 8 hoặc cao hơn
- Maven 3.6.0 hoặc cao hơn
- Apache Tomcat 9 hoặc tương đương
- Trình duyệt web hiện đại

## Cách build dự án

### Build Server

```bash
cd websocket-server-tomcat
mvn clean package
```

Sau khi build thành công, file WAR sẽ được tạo tại `target/websocket-server.war`.

### Build Client

```bash
cd websocket-gwt-client
mvn clean package
```

Sau khi build thành công, file WAR sẽ được tạo tại `target/websocket-gwt-client.war`.

## Cách triển khai

### Triển khai lên Apache Tomcat

1. Sao chép các file WAR vào thư mục webapps của Tomcat:
   ```
   copy websocket-server-tomcat/target/websocket-server.war [Tomcat_Home]/webapps/
   copy websocket-gwt-client/target/websocket-gwt-client.war [Tomcat_Home]/webapps/
   ```
   
2. Khởi động hoặc khởi động lại Tomcat (nếu chưa chạy):
   ```
   [Tomcat_Home]/bin/startup.bat    # Windows
   [Tomcat_Home]/bin/startup.sh     # Linux/Mac
   ```

### Truy cập ứng dụng

1. Truy cập ứng dụng client GWT:
   ```
   http://localhost:8080/websocket-gwt-client/WebSocketClient.html
   ```

2. Endpoint WebSocket sẽ có sẵn tại:
   ```
   ws://localhost:8080/websocket-server/notification-ws
   ```

## Cách sử dụng

1. Mở trang client GWT trong trình duyệt
2. Nhấn nút "Kết nối" để thiết lập kết nối WebSocket với server
3. Khi đã kết nối thành công, bạn có thể gửi tin nhắn và nhận phản hồi
4. Máy chủ sẽ tự động gửi thông báo theo định kỳ (mỗi 2 phút)
5. Sử dụng nút "Xóa tin nhắn" để làm sạch bảng tin nhắn
6. Nhấn "Ngắt kết nối" để đóng kết nối WebSocket

## Kiểm tra kết nối trực tiếp với server

Bạn có thể kiểm tra kết nối WebSocket từ bất kỳ client WebSocket nào bằng cách kết nối đến:
```
ws://localhost:8080/websocket-server/notification-ws
```

## Xử lý sự cố

- Nếu không thể kết nối, kiểm tra xem Tomcat đã khởi động thành công chưa
- Xác nhận rằng cả hai ứng dụng (server và client) đã được triển khai đúng cách
- Kiểm tra log của Tomcat tại `[Tomcat_Home]/logs/catalina.out` để tìm lỗi
- Trong trình duyệt, mở Developer Tools (F12) để xem log và lỗi WebSocket