/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/4/12 0012
 */
package com.redis.dao;

import dto.UserDto;

/**
 * name UserDao
 * author ZhangLinFeng
 * data 2017/4/12 0012
 * 用户的基本操作
 */
public interface UserDao {

    /**
     * 插入
     * @param user
     */
    void insert(UserDto user);


    /**
     * 查询
     * @param id
     * @return
     */
    UserDto selectById(String id);


}
