package com.chat.chatserver.config;

import com.chat.chatserver.dto.ResponseDto;

/**
 * Create by Guolianxing on 2018/7/5.
 */
public class SystemConstant {

    public static final String USERNAME_EXIST = "该用户名已存在";

    public static final String USER_UNEXIST = "该用户不存在";

    public static final String PASSWORD_ERROR = "密码错误";

    public static final String OP_SUCCESS_MSG = "操作成功";

    public static final String SERVER_EXCEPTION = "服务器异常";

    public static final String ROOM_EXIST = "该聊天室已存在";

    public static final String ROOM_UNEXIST = "该聊天室不存在";

    public static final String USERNAEM_STYLE_ERROR = "用户名为中文，字母，数字组合，\n长度为2-10";

    public static final String ROOMNAME_TOO_LONG = "聊天室名长度不超过10个字符";

    public static final String ROOMNAME_BLANK = "聊天室名不能为空";

    public static final Integer SUCCESS = 0;

    public static final Integer FAIL = -1;

    public static final ResponseDto OP_SUCCESS = new ResponseDto(SystemConstant.SUCCESS, SystemConstant.OP_SUCCESS_MSG);

    public static final ResponseDto OP_FAIL = new ResponseDto(SystemConstant.FAIL, SystemConstant.SERVER_EXCEPTION);

    public static final String SERVER_HOST = "http://47.104.246.160:8080";
}
