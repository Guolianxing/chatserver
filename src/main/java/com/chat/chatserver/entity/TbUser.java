package com.chat.chatserver.entity;

import net.sf.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

/**
 * Create by Guolianxing on 2018/7/5.
 */
public class TbUser {

    private Integer userId;
    private String username;
    private String password;
    private String photo;
    private String sessionId;

    private WebSocketSession socketSession;

    public TbUser() {
    }

    public TbUser(Integer userId, String username, String password, String photo, String sessionId, WebSocketSession socketSession) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.photo = photo;
        this.sessionId = sessionId;
        this.socketSession = socketSession;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public WebSocketSession getSocketSession() {
        return socketSession;
    }

    public void setSocketSession(WebSocketSession socketSession) {
        this.socketSession = socketSession;
    }

    @Override
    public String toString() {
        return "TbUser{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("userId", userId);
        jsonObject.accumulate("username", username);
        jsonObject.accumulate("password", password);
        jsonObject.accumulate("photo", photo);
        return jsonObject;
    }
}
