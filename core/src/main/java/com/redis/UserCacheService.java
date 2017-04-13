/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/4/11 0011
 */
package com.redis;

import pojo.Person;

/**
 * name UserCacheService
 * author ZhangLinFeng
 * data 2017/4/11 0011
 * 用户缓存...
 */
public interface UserCacheService {

    /**
     * 创建用户
     * @param person
     */
    Person createUser(Person person);


    /**
     * 根据用户名查询
     * @param name
     * @return
     */
    Person findByName(String  name);


}
