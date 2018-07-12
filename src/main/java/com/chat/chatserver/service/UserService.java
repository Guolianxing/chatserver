package com.chat.chatserver.service;

import com.chat.chatserver.config.SystemConstant;
import com.chat.chatserver.dto.MsgDto;
import com.chat.chatserver.dto.ResponseDto;
import com.chat.chatserver.entity.TbUser;
import com.chat.chatserver.repository.UserRepository;
import com.chat.chatserver.util.SessionContext;
import com.chat.chatserver.util.TimeUtil;
import com.chat.chatserver.websocket.SocketHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Create by Guolianxing on 2018/7/5.
 */
@Service
@Transactional
public class UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private SocketHandler socketHandler;

    private static final String key = "chatserver";

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    /**
     * @Description: 注册
     * @Author: Guolianxing
     * @Date: 2018/7/5 18:39
     */
    public ResponseDto register(TbUser user) throws Exception {
        ResponseDto response = new ResponseDto();
        Integer count = userRepository.countByUsername(user.getUsername());
        if (count > 0) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.USERNAME_EXIST);
            return response;
        }

        String reg =  "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}$";
        Pattern pattern = Pattern.compile(reg);

        if (!pattern.matcher(user.getUsername()).matches()) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.USERNAEM_STYLE_ERROR);
            return response;
        }


        String password = DigestUtils.md5DigestAsHex((user.getPassword() + key).getBytes("UTF-8"));
        user.setPassword(password);

        Random random = new Random();
        Integer num = random.nextInt(6) + 1;

        String img = SystemConstant.SERVER_HOST + "/imgs/default0" + num + ".png";

        user.setPhoto(img);

        userRepository.addUser(user);

        return SystemConstant.OP_SUCCESS;
    }


    /**
     * @Description: 登录
     * @Author: Guolianxing
     * @Date: 2018/7/5 18:39
     */
    public ResponseDto login(String username, String password) throws Exception {
        TbUser user = userRepository.getUserByName(username);
        ResponseDto response = new ResponseDto();
        if (user.getUserId() == null) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.USER_UNEXIST);
            return response;
        }

        String hex = DigestUtils.md5DigestAsHex((password + key).getBytes("UTF-8"));

        if (!hex.equals(user.getPassword())) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.PASSWORD_ERROR);
            return response;
        }

        SessionContext context = SessionContext.getInstance();

        if (context.isLogin(username)) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.USER_HAS_LOGIN);
            return response;
        }

        String sessionId = UUID.randomUUID().toString().replace("-", "");


        context.addSession(sessionId, user);

        response.setStatus(SystemConstant.SUCCESS);
        response.setMsg(SystemConstant.OP_SUCCESS_MSG);
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("token", sessionId);
        jsonObject.accumulate("user", user.toJSON());
        response.setData(jsonObject);

        logger.info("用户登录: {}", user.toJSON());

        return response;
    }

    /**
     * @Description: 退出登录
     * @Author: Guolianxing
     * @Date: 2018/7/5 18:39
     */
    public ResponseDto logout(String token) throws Exception {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(SystemConstant.SUCCESS);
        responseDto.setMsg(SystemConstant.OP_SUCCESS_MSG);
        SessionContext context = SessionContext.getInstance();
        TbUser session = context.getSession(token);
        if (session == null || session.getSocketSession() == null) {
            return responseDto;
        }

        WebSocketSession socketSession = session.getSocketSession();
        if (socketSession.isOpen()) {
            socketSession.close();
        }

        return responseDto;
    }

    /**
     * @Description: 创建聊天室
     * @Author: Guolianxing
     * @Date: 2018/7/5 18:39
     */
    public ResponseDto createRoom(String roomName) throws Exception {
        ResponseDto response = new ResponseDto();
        if ("".equals(roomName.trim())) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.ROOMNAME_BLANK);
            return response;
        }
        if (roomName.length() > 10) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.ROOMNAME_TOO_LONG);
            return response;
        }

        Set<String> roomSet = SocketHandler.rooms.keySet();
        if (roomSet.contains(roomName)) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.ROOM_EXIST);
            return response;
        }

        Set<WebSocketSession> set = Collections.synchronizedSet(new HashSet<>());
        SocketHandler.rooms.put(roomName, set);

        logger.info("创建聊天室：{}", roomName);

        return SystemConstant.OP_SUCCESS;
    }

    /**
     * @Description: 用户进入聊天室
     * @Author: Guolianxing
     * @Date: 2018/7/5 18:51
     * TODO 用户进入聊天室后要向整个聊天室广播用户进入通知
     */
    public ResponseDto enterRoom(String token, String roomName) throws Exception {
        ResponseDto response = new ResponseDto();
        SessionContext context = SessionContext.getInstance();
        TbUser session = context.getSession(token);
        if (session == null || session.getSocketSession() == null) {
            throw new Exception("用户进入聊天室时出错！");
        }

        Set<String> roomSet = SocketHandler.rooms.keySet();
        if (!roomSet.contains(roomName)) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.ROOM_UNEXIST);
            return response;
        }

        Set<WebSocketSession> users = SocketHandler.rooms.get(roomName);
        users.add(session.getSocketSession());

        MsgDto msgDto = new MsgDto();
        msgDto.setMsgType(1);
        msgDto.setRoomName(roomName);
        msgDto.setSendTime(TimeUtil.dateTime());
        msgDto.setMsg("用户" + session.getUsername() + "进入聊天室");
        logger.info("用户 {} 进入聊天室 {}", session.getUsername(), roomName);
        socketHandler.sendMsg(msgDto);
        return SystemConstant.OP_SUCCESS;
    }

    /**
     * @Description: 用户离开聊天室
     * @Author: Guolianxing
     * @Date: 2018/7/5 19:10
     * TODO 用户离开聊天室后要向整个聊天室广播用户进入通知
     */
    public ResponseDto leavaRoom(String token, String roomName) throws Exception {
        ResponseDto response = new ResponseDto();
        SessionContext context = SessionContext.getInstance();
        TbUser session = context.getSession(token);
        if (session == null || session.getSocketSession() == null) {
            if (session != null) {
                context.delSession(token);
            }
            throw new Exception("用户离开聊天室时出错！");
        }

        Set<String> roomSet = SocketHandler.rooms.keySet();
        if (!roomSet.contains(roomName)) {
            response.setStatus(SystemConstant.FAIL);
            response.setMsg(SystemConstant.ROOM_UNEXIST);
            return response;
        }

        Set<WebSocketSession> users = SocketHandler.rooms.get(roomName);
        users.remove(session.getSocketSession());
        if (users.size() == 0) {
            SocketHandler.rooms.remove(roomName);
        }

        MsgDto msgDto = new MsgDto();
        msgDto.setMsgType(2);
        msgDto.setRoomName(roomName);
        msgDto.setSendTime(TimeUtil.dateTime());
        msgDto.setMsg("用户" + session.getUsername() + "离开聊天室");
        logger.info("用户 {} 离开聊天室 {}", session.getUsername(), roomName);
        socketHandler.sendMsg(msgDto);
        return SystemConstant.OP_SUCCESS;
    }

    /**
     * @Description: 获取所有聊天室信息
     * @Author: Guolianxing
     * @Date: 2018/7/6 22:32
     */
    public ResponseDto getAllRoom() throws Exception {
        ResponseDto responseDto = new ResponseDto();
        JSONArray jsonArray = new JSONArray();
        Set<String> rooms = SocketHandler.rooms.keySet();
        for (String roomName : rooms) {
            Set<WebSocketSession> room = SocketHandler.rooms.get(roomName);
            if (room != null) {
                JSONObject json = new JSONObject();
                json.accumulate("roomName", roomName);
                json.accumulate("num", room.size());
                jsonArray.add(json);
            }
        }
        responseDto.setStatus(SystemConstant.SUCCESS);
        responseDto.setMsg(SystemConstant.OP_SUCCESS_MSG);
        responseDto.setData(jsonArray);

        logger.info("获取所有聊天室信息: {}", jsonArray.toString());
        return responseDto;
    }

    /**
     * @Description: 获取聊天室内所有用户
     * @Author: Guolianxing
     * @Date: 2018/7/7 18:52
     */
    public ResponseDto getAllUsersInRoom(String roomName) throws Exception {
        ResponseDto responseDto = new ResponseDto();
        if (!SocketHandler.rooms.containsKey(roomName)) {
            responseDto.setStatus(SystemConstant.FAIL);
            responseDto.setMsg(SystemConstant.SERVER_EXCEPTION);
            return responseDto;
        }
        Set<WebSocketSession> set = SocketHandler.rooms.get(roomName);
        JSONArray jsonArray = new JSONArray();
        SessionContext sessionContext = SessionContext.getInstance();
        for (WebSocketSession socketSession : set) {
            TbUser tbUser = sessionContext.getSession(SocketHandler.userSessionMap.get(socketSession.getId()));
            if (tbUser != null) {
                JSONObject json = new JSONObject();
                json.accumulate("username", tbUser.getUsername());
                json.accumulate("photo", tbUser.getPhoto());
                jsonArray.add(json);
            }
        }
        responseDto.setStatus(SystemConstant.SUCCESS);
        responseDto.setMsg(SystemConstant.OP_SUCCESS_MSG);
        responseDto.setData(jsonArray);

        logger.info("获取聊天室所有用户: {}", jsonArray.toString());
        return responseDto;
    }
}
