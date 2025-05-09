# java-websocket-gwt
Sample using websocket to push notification
Cách triển khai và chạy
Server:

Biên dịch dự án với Maven: mvn clean package
Triển khai file WAR đến Tomcat (folder webapps)
Server WebSocket sẽ sẵn sàng tại đường dẫn: ws://localhost:8080/websocket-server/notification-ws
Client:

Biên dịch dự án với Maven: mvn clean package
Triển khai file WAR đến Tomcat(folder webapps)
Truy cập ứng dụng tại: http://localhost:8080/websocket-gwt-client/WebSocketClient.html
Click nút "Kết nối" để thiết lập kết nối WebSocket đến server