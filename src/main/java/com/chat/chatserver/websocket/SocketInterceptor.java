package com.chat.chatserver.websocket;

import com.chat.chatserver.entity.TbUser;
import com.chat.chatserver.util.SessionContext;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Create by Guolianxing on 2018/7/5.
 * websocket拦截器
 */
public class SocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {

        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
            String sessionId = request.getServletRequest().getParameter("token");
            if (sessionId == null) {
                return false;
            }
            SessionContext context = SessionContext.getInstance();
            TbUser session = context.getSession(sessionId);
            if (session == null) {
                return false;
            }

            // 回话标识放入socketSession中
            map.put("sessionId", sessionId);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
