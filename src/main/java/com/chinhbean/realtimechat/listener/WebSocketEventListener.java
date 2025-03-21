package com.chinhbean.realtimechat.listener;

import com.chinhbean.realtimechat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
/*
* Đây là một listener (bộ lắng nghe) để bắt các sự kiện WebSocket
* Nó giúp ghi log khi có người kết nối hoặc rời khỏi WebSocket, đồng thời gửi thông báo khi ai đó rời khỏi phòng chat.
*
* Lắng nghe sự kiện khi một client kết nối vào WebSocket.
* Lắng nghe sự kiện khi một client rời khỏi WebSocket.
 * */

public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    //là một công cụ giúp gửi tin nhắn qua WebSocket
    private SimpMessageSendingOperations messagingTemplate;


    // @EventListener là phương thức lắng nghe sự kiện trong Spring
    @EventListener
    /*
    * SessionConnectedEvent:
    Đây là sự kiện được bắn ra khi một client kết nối thành công vào WebSocket.
    */
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            logger.info("User Disconnected : " + username);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            chatMessage.setStatus("offline");
            chatMessage.setAvatarUrl("https://example.com/avatars/" + username + ".png");
            messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);
        }
    }

}
