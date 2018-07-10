package com.chat.chatserver;

import com.chat.chatserver.entity.TbUser;
import com.chat.chatserver.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatServerApplicationTests {

    @Resource
    private UserRepository userRepository;

    @Test
    public void contextLoads() {

    }

}
