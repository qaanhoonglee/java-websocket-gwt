#!/usr/bin/env bash

echo "===== Building WebSocket Server ====="
cd "$(dirname "$0")"
cd websocket-server-tomcat
mvn clean package
echo
echo "===== Build Complete ====="
echo "Check target/websocket-server.war"
