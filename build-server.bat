@echo off
echo ===== Building WebSocket Server =====
cd /d %~dp0
cd websocket-server-tomcat
call mvn clean package
echo.
echo ===== Build Complete =====
echo Check target/websocket-server.war 
