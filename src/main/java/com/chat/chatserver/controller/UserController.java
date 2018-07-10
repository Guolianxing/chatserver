package com.chat.chatserver.controller;

import com.chat.chatserver.dto.ResponseDto;
import com.chat.chatserver.entity.TbUser;
import com.chat.chatserver.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Create by Guolianxing on 2018/7/5.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    private UserService userService;


    /**
     * @Description: 用户注册
     * @Author: Guolianxing
     * @Date: 2018/7/5 15:14
     */
    @RequestMapping(value = "/register")
    public ResponseDto registerUser(TbUser user) throws Exception {
        return userService.register(user);
    }

    /**
     * @Description: 用户登录
     * @Author: Guolianxing
     * @Date: 2018/7/5 15:27
     */
    @RequestMapping(value = "/login")
    public ResponseDto login(@RequestParam("username") String username, @RequestParam("password") String password) throws Exception {
        return userService.login(username, password);
    }

    /**
     * @Description: 退出登录
     * @Author: Guolianxing
     * @Date: 2018/7/5 18:24
     */
    @RequestMapping(value = "/logout")
    public ResponseDto logout(@RequestParam("token") String token) throws Exception {
        return userService.logout(token);
    }

    /**
     * @Description: 创建聊天室
     * @Author: Guolianxing
     * @Date: 2018/7/5 18:46
     */
    @RequestMapping(value = "/createRoom")
    public ResponseDto createRoom(@RequestParam("roomName") String roomName) throws Exception {
        return userService.createRoom(roomName);
    }

    /**
     * @Description: 用户进入聊天室
     * @Author: Guolianxing
     * @Date: 2018/7/5 19:00
     */
    @RequestMapping(value = "/enterRoom")
    public ResponseDto enterRoom(@RequestParam("token") String token, @RequestParam("roomName") String roomName) throws Exception {
        return userService.enterRoom(token, roomName);
    }

    /**
     * @Description: 用户离开聊天室
     * @Author: Guolianxing
     * @Date: 2018/7/5 19:15
     */
    @RequestMapping(value = "/leaveRoom")
    public ResponseDto leaveRoom(@RequestParam("token") String token, @RequestParam("roomName") String roomName) throws Exception {
        return userService.leavaRoom(token, roomName);
    }

    /**
     * @Description: 获取所有聊天室信息（聊天室名和人数）
     * @Author: Guolianxing
     * @Date: 2018/7/6 22:37
     */
    @RequestMapping(value = "/getAllRoom")
    public ResponseDto getAllRoom() throws Exception {
        return userService.getAllRoom();
    }

    /**
     * @Description: 获取聊天室内所有用户
     * @Author: Guolianxing
     * @Date: 2018/7/7 19:05
     */
    @RequestMapping(value = "/getUsersInRoom")
    public ResponseDto getAllUsersInRoom(@RequestParam("roomName") String roomName) throws Exception {
        return userService.getAllUsersInRoom(roomName);
    }
}
