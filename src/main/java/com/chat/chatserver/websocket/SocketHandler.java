package com.chat.chatserver.websocket;

import com.chat.chatserver.dto.MsgDto;
import com.chat.chatserver.entity.TbUser;
import com.chat.chatserver.util.SessionContext;
import com.chat.chatserver.util.TimeUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.*;

/**
 * Create by Guolianxing on 2018/7/5.
 */
@Component
public class SocketHandler extends AbstractWebSocketHandler {

    // 这里要用线程安全的集合类
    public static Map<String, Set<WebSocketSession>> rooms = Collections.synchronizedMap(new HashMap<>());

    // socketSessionId与会话管理sessionId的映射表
    public static Map<String, String> userSessionMap = Collections.synchronizedMap(new HashMap<>());

    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        logger.info("建立连接");

        // 取出会话标识
        String userSessionId = getSessionId(session);
        if (userSessionId == null) {
            session.close();
        }
        SessionContext sessionContext = SessionContext.getInstance();
        TbUser user = sessionContext.getSession(userSessionId);
        if (user == null) {
            session.close();
        }
        user.setSocketSession(session);
        userSessionMap.put(session.getId(), userSessionId);
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        JSONObject jsonObject = JSONObject.fromObject(msg);
        sendMsg(jsonObject);
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        String userSessionId = userSessionMap.get(session.getId());
        SessionContext sessionContext = SessionContext.getInstance();

        // 如果关闭连接时用户在一个聊天室里，则将他从聊天室移出
        // 用于客户端意外中断，比如被任务管理器杀死
        Set<String> keySet = rooms.keySet();
        for (String key : keySet) {
            Set<WebSocketSession> set = rooms.get(key);
            if (set.contains(session)) {
                set.remove(session);
                MsgDto msgDto = new MsgDto();
                msgDto.setMsgType(2);
                msgDto.setRoomName(key);
                msgDto.setSendTime(TimeUtil.dateTime());
                msgDto.setMsg("用户" + sessionContext.getSession(userSessionId).getUsername() + "离开聊天室");
                sendMsg(msgDto);
                // 聊天室人数为0，删除
                if (set.size() == 0) {
                    rooms.remove(key);
                }
                break;
            }
        }
        // 清除会话信息
        sessionContext.delSession(userSessionId);

        // 清除该socketSessionId与会话sessionId映射
        userSessionMap.remove(session.getId());
        super.afterConnectionClosed(session, status);
    }

    private String getSessionId(WebSocketSession socketSession) {
        String sessionId = null;
        try {
            sessionId = (String) socketSession.getAttributes().get("sessionId");
        } catch (Exception e) {
            return null;
        }
        return sessionId;
    }

    public void sendMsg(MsgDto msgDto) {

        JSONObject json = JSONObject.fromObject(msgDto);
        sendMsg(json);
    }

    public void sendMsg(JSONObject jsonObject) {
        if (jsonObject.containsKey("roomName")) {
            String roomName = jsonObject.getString("roomName");
            Set<WebSocketSession> users = rooms.get(roomName);
            if (users != null) {
                for (WebSocketSession session : users) {
                    if (session != null && session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(jsonObject.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

}
