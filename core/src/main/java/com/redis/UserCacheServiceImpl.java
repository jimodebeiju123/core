/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/4/11 0011
 */
package com.redis;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pojo.Person;

/**
 * @author ZhangLinFeng
 * @name UserCacheServiceImpl
 * @data 2017/4/11 0011
 */
@Service("userCacheService")
public class UserCacheServiceImpl implements UserCacheService {


    /**
     * 创建用户
     *
     * @param person
     */
    @Override
    @CachePut(value = "core",key = "'userCacheService[name-'+#person.name")
    public Person createUser(Person person) {
        return person;
    }

    /**
     * 根据用户名查询
     *
     * @param name
     * @return
     */
    @Override
    @Cacheable(value = "core",key = "'userCacheService[name-'+#name")
    public Person findByName(String name) {
        return null;
    }
}
