package com.chat.chatserver.controller;

import com.chat.chatserver.config.SystemConstant;
import com.chat.chatserver.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Create by Guolianxing on 2018/7/5.
 */
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * @Description: 捕获controller抛出的异常，返回服务器异常提示
     * @Author: Guolianxing
     * @Date: 2018/7/5 15:53
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseDto excetionHandler(Exception e) {
        logger.info("Throw Exception: {}", e.getMessage());
        return SystemConstant.OP_FAIL;
    }

}
