package com.chat.chatserver.util;

import com.chat.chatserver.entity.TbUser;

import java.util.Collections;
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
        map = Collections.synchronizedMap(new HashMap<>());
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

    public void delSession(String sessionId) {
        if (sessionId != null) {
            map.remove(sessionId);
        }
    }

    public void addSession(String sessionId, TbUser user) {
        if (sessionId != null && user != null) {
            map.put(sessionId, user);
        }
    }

    public TbUser getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return map.get(sessionId);
    }

    public Boolean isLogin(String sessionId) {
        TbUser tbUser = getSession(sessionId);
        return tbUser != null;
    }


}
