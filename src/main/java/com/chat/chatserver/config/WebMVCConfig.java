package com.chat.chatserver.config;

import com.chat.chatserver.util.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Guolianxing on 2018/7/5.
 */
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
        excludePath.add("/user/login");
        excludePath.add("/user/register");
        excludePath.add("/imgs/*");
        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/**").excludePathPatterns(excludePath);
    }
}
