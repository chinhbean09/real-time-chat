package com.chinhbean.realtimechat.controller;

import com.chinhbean.realtimechat.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {


    //@MessageMapping: Định nghĩa endpoint WebSocket.
    @MessageMapping("/chat.sendMessage")
    //@SendTo: Gửi dữ liệu đến topic cho tất cả client đã subscribe.
    @SendTo("/topic/publicChatRoom")

    /*
      1. Khi client gửi tin nhắn đến /app/chat.sendMessage, method này sẽ được gọi.
      2. Gửi tin nhắn đến /topic/publicChatRoom, tất cả người tham gia phòng sẽ nhận được tin nhắn.
      3. Trả về ChatMessage để gửi đến tất cả client đã subscribe.
     */
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/publicChatRoom")

    /*
        1. Khi client gửi dữ liệu đến /app/chat.addUser, method này sẽ được gọi.
        2. Lưu username vào session WebSocket.
        3. Thông báo đến phòng chat rằng user mới đã tham gia.
     */
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
