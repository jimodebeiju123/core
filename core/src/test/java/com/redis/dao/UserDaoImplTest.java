package com.redis.dao;

import dto.UserDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 * UserDaoImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:spring/spring-*.xml"})
public class UserDaoImplTest {

    @Autowired
    private UserDao userDao;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: insert(UserDto user)
     */
    @Test
    public void testInsert() throws Exception {
        UserDto user=new UserDto();
        user.setId(UUID.randomUUID().toString());
        user.setUserName("zlf");
        user.setPassword("qweejcjdajdwajidawuidawduu2321");
        userDao.insert(user);
    }

    /**
     * Method: selectById(String id)
     */
    @Test
    public void testSelectById() throws Exception {
        UserDto user=userDao.selectById("8f81caa0-ccbd-425c-8741-9003fda28d5e");
        System.out.println(user);
    }


} 
