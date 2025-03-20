package com.chinhbean.realtimechat.model;

import java.util.List;

public class ChatMessage {

    private MessageType type;
    private String content;
    private String sender;
    private String recipient;
    // Thêm getter và setter
    private String avatarUrl; // URL của avatar
    private String status;    // "online" hoặc "offline"
    private String mediaUrl; // URL của tệp sau khi upload
    private String mediaType; // "image", "video", "file"
    private List<String> roomUsers; // Danh sách người dùng trong phòng

    public List<String> getRoomUsers() {
        return roomUsers;
    }

    public void setRoomUsers(List<String> roomUsers) {
        this.roomUsers = roomUsers;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String roomId; // Thêm trường này nếu cần
    private String imageUrl; // URL của hình ảnh
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public enum MessageType {
        CHAT, JOIN, LEAVE, PRIVATE // Thêm PRIVATE cho tin nhắn riêng
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

}
