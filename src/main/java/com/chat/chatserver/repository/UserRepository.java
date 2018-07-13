package com.chat.chatserver.repository;

import com.chat.chatserver.entity.TbUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Create by Guolianxing on 2018/7/5.
 */
@Repository
public class UserRepository {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public Integer addUser(TbUser user) {
        String sql = "insert into tb_user (username, password, photo) values (?, ?, ?) ";
        Object[] params = new Object[]{user.getUsername(), user.getPassword(), user.getPhoto()};
        return jdbcTemplate.update(sql, params);
    }

    public Integer countByUsername(String username) {
        String sql = "select count(*) from tb_user where username = ? ";
        Object[] params = new Object[]{username};
        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public TbUser getUserByName(String username) {
        String sql = "select * from tb_user where username = ? ";
        Object[] params = new Object[]{username};
        final TbUser user = new TbUser();
        jdbcTemplate.query(sql, params, resultSet -> {
            user.setUserId(resultSet.getInt("userId"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setPhoto(resultSet.getString("photo"));
            user.setSessionId(resultSet.getString("sessionId"));
        });
        return user;
    }

    public Integer updateSessionId(String sessionId, Integer userId) {
        String sql = "update tb_user set sessionId = ? where userId = ? ";
        Object[] params = new Object[]{sessionId, userId};
        return jdbcTemplate.update(sql, params);
    }


}
