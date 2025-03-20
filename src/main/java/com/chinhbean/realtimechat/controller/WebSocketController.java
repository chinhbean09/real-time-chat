package com.chinhbean.realtimechat.controller;

import com.chinhbean.realtimechat.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private final Map<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    @MessageMapping("/chat.sendMessage")
    @org.springframework.messaging.handler.annotation.SendTo("/topic/publicChatRoom")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setStatus("online");
        chatMessage.setAvatarUrl("https://example.com/avatars/" + chatMessage.getSender() + ".png");
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @org.springframework.messaging.handler.annotation.SendTo("/topic/publicChatRoom")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setStatus("online");
        chatMessage.setAvatarUrl("https://example.com/avatars/" + chatMessage.getSender() + ".png");
        return chatMessage;
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.PRIVATE);
        chatMessage.setStatus("online");
        chatMessage.setAvatarUrl("https://example.com/avatars/" + chatMessage.getSender() + ".png");
        messagingTemplate.convertAndSend("/user/" + chatMessage.getRecipient() + "/queue/private", chatMessage);
        messagingTemplate.convertAndSend("/user/" + chatMessage.getSender() + "/queue/private", chatMessage);
    }

    @MessageMapping("/chat.createPrivateRoom")
    public void createPrivateRoom(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String roomId = UUID.randomUUID().toString();
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent("Private room created with ID: " + roomId);
        chatMessage.setStatus("online");
        chatMessage.setAvatarUrl("https://example.com/avatars/" + chatMessage.getSender() + ".png");
        headerAccessor.getSessionAttributes().put("roomId", roomId);

        roomUsers.computeIfAbsent(roomId, k -> new HashSet<>()).add(chatMessage.getSender());
        chatMessage.setRoomUsers(new ArrayList<>(roomUsers.get(roomId)));

        messagingTemplate.convertAndSend("/user/" + chatMessage.getSender() + "/queue/private", chatMessage);
    }

    @MessageMapping("/chat.joinRoom")
    public void joinRoom(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String roomId = chatMessage.getRoomId();
        String username = chatMessage.getSender();

        if (roomId != null && username != null) {
            headerAccessor.getSessionAttributes().put("roomId", roomId);
            roomUsers.computeIfAbsent(roomId, k -> new HashSet<>()).add(username);

            chatMessage.setType(ChatMessage.MessageType.JOIN);
            chatMessage.setContent(username + " joined the room!");
            chatMessage.setStatus("online");
            chatMessage.setAvatarUrl("https://example.com/avatars/" + username + ".png");
            chatMessage.setRoomUsers(new ArrayList<>(roomUsers.get(roomId)));

            messagingTemplate.convertAndSend("/room/" + roomId, chatMessage);
        }
    }

    @MessageMapping("/chat.leaveRoom")
    public void leaveRoom(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
        String username = chatMessage.getSender();

        if (roomId != null && username != null) {
            roomUsers.getOrDefault(roomId, new HashSet<>()).remove(username);

            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setContent(username + " left the room!");
            chatMessage.setStatus("offline");
            chatMessage.setAvatarUrl("https://example.com/avatars/" + username + ".png");
            chatMessage.setRoomUsers(new ArrayList<>(roomUsers.getOrDefault(roomId, new HashSet<>())));

            messagingTemplate.convertAndSend("/room/" + roomId, chatMessage);
            headerAccessor.getSessionAttributes().remove("roomId");
        }
    }

    @MessageMapping("/chat.sendRoomMessage")
    public void sendRoomMessage(@Payload ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId();
        if (roomId != null) {
            chatMessage.setType(ChatMessage.MessageType.CHAT);
            chatMessage.setStatus("online");
            chatMessage.setAvatarUrl("https://example.com/avatars/" + chatMessage.getSender() + ".png");
            messagingTemplate.convertAndSend("/room/" + roomId, chatMessage);
        }
    }

}