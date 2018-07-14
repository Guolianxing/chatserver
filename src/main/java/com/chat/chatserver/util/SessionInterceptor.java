package com.chat.chatserver.util;

import com.chat.chatserver.entity.TbUser;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by Guolianxing on 2018/7/5.
 * http请求拦截器
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionId = request.getParameter("token");
        if (sessionId == null) {
            return false;
        }

        SessionContext context = SessionContext.getInstance();
        TbUser session = context.getSession(sessionId);

        return session != null;
    }
}
