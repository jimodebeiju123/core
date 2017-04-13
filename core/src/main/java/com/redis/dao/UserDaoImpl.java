/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/4/12 0012
 */
package com.redis.dao;

import com.alibaba.fastjson.JSON;
import dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ZhangLinFeng
 * @name UserDaoImpl
 * @data 2017/4/12 0012
 */
@Component("userDao")
public class UserDaoImpl implements UserDao {

    private Logger logger= LoggerFactory.getLogger(UserDaoImpl.class);

    @Resource(name="redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 插入
     *
     * @param user
     */
    @Override
    public void insert(UserDto user) {
        String key=user.getId();
        String value= JSON.toJSONString(user);
        redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key.getBytes(),value.getBytes());
                return null;
            }
        });
    }



    /**
     * 查询
     *
     * @param id
     * @return
     */
    @Override
    public UserDto selectById(String id) {
        return redisTemplate.execute(new RedisCallback<UserDto>() {
            @Override
            public UserDto doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] value= connection.get(id.getBytes());
                if(value.length>0){
                    return JSON.parseObject(value,UserDto.class);
                }
                return null;
            }
        });
    }
}
