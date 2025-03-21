package com.chinhbean.realtimechat.config;

import com.chinhbean.realtimechat.interceptor.HttpHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration

//Annotation này nói với Spring rằng hãy bật (enable) WebSocket Server.\
//Nó giúp chúng ta có thể sử dụng STOMP để gửi và nhận tin nhắn thay vì làm việc với WebSocket thuần.
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //Interceptor này có thể kiểm tra JWT Token, session, user, v.v.
    @Autowired
    private HttpHandshakeInterceptor handshakeInterceptor;

    //Định nghĩa STOMP Endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //Định nghĩa một WebSocket endpoint có đường dẫn /ws.
        // => Client sẽ kết nối tới ws://localhost:8080/ws hoặc wss://yourdomain.com/ws.
        registry.addEndpoint("/ws").withSockJS().setInterceptors(handshakeInterceptor);
    }

    //Cấu hình Message Broker
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
        Các tin nhắn từ client gửi đến server phải bắt đầu với "/app".

        Ví dụ: Khi client gửi tin nhắn đến server:
        stompClient.send("/app/chat", {}, JSON.stringify({ message: "Hello" }));
        Khi đó, server sẽ nhận tin nhắn ở /app/chat và xử lý.
        */
//        registry.setApplicationDestinationPrefixes("/app");


        /*
        Cho phép sử dụng Message Broker để gửi tin nhắn từ server về client.
        Client sẽ subscribe (lắng nghe) các kênh có tiền tố "/topic".
        Client đã subscribe vào /topic/... sẽ nhận được tin nhắn ngay lập tức.

        Ví dụ: Khi server gửi tin nhắn đến tất cả client:
        messagingTemplate.convertAndSend("/topic/messages", "Hello, Clients!");
        */
//        registry.enableSimpleBroker("/topic", "/private");
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue", "/room"); // Thêm /room cho phòng riêng
        registry.setUserDestinationPrefix("/user");
    }

}
