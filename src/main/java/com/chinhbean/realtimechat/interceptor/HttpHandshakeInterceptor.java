package com.chinhbean.realtimechat.interceptor;

import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component

/*
    HttpHandshakeInterceptor là một interceptor trong WebSocket
    Được sử dụng để can thiệp vào quá trình bắt tay (handshake) giữa client và server
    Nó giúp lấy thông tin từ request ban đầu và lưu vào attributes của WebSocket session, để có thể sử dụng về sau.
*/

public class HttpHandshakeInterceptor implements HandshakeInterceptor {
/*
  Khi một client kết nối đến WebSocket,
  nó thực hiện một handshake HTTP ban đầu trước khi kết nối WebSocket thực sự được thiết lập.

    + Lấy thông tin session ID.
    + Kiểm tra token xác thực.
    + Ngăn chặn các kết nối không hợp lệ.

*/

    private static final Logger logger = LoggerFactory.getLogger(HttpHandshakeInterceptor.class);


    /*
    HandshakeInterceptor có hai phương thức chính:
    * beforeHandshake(): Chạy trước khi handshake hoàn tất.
    * afterHandshake(): Chạy sau khi handshake hoàn tất.
    */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        //log kiểm tra xem interceptor có được gọi hay không
        logger.info("Call beforeHandshake");

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            //Lấy sessionId và lưu vào attributes:
            HttpSession session = servletRequest.getServletRequest().getSession();

            //Sau này, trong WebSocket session, ta có thể lấy sessionId từ attributes.
            attributes.put("sessionId", session.getId());

            /*
            Giúp nhận diện người dùng khi họ gửi tin nhắn.
            Cho phép lấy thông tin session của người dùng trong WebSocket handler.
            */

        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        logger.info("Call afterHandshake");
    }

}
