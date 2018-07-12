package com.chat.chatserver.util;

import com.chat.chatserver.entity.TbUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Create by Guolianxing on 2018/7/5.
 * @Description: 单例全局map，用于会话管理
 */
public class SessionContext {

    private Map<String, TbUser> map = null;

    private static SessionContext sessionContext = null;

    private SessionContext() {
        map = new HashMap<>();
    }

    public static SessionContext getInstance() {
        if (sessionContext == null) {
            synchronized (SessionContext.class) {
                if (sessionContext == null) {
                    sessionContext = new SessionContext();
                }
            }
        }
        return sessionContext;
    }

    public synchronized void delSession(String sessionId) {
        if (sessionId != null) {
            map.remove(sessionId);
        }
    }

    public synchronized void addSession(String sessionId, TbUser user) {
        if (sessionId != null && user != null) {
            map.put(sessionId, user);
        }
    }

    public synchronized TbUser getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return map.get(sessionId);
    }

    public synchronized Boolean isLogin(String username) {
        Set<String> users = map.keySet();
        for (String name : users) {
            if (username.equals(map.get(name).getUsername())) {
                return true;
            }
        }
        return false;
    }


}
