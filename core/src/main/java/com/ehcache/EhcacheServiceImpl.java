/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/4/10 0010
 */
package com.ehcache;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author ZhangLinFeng
 * @name EhcacheServiceImpl
 * @data 2017/4/10 0010
 */
@Service("ehcacheService")
public class EhcacheServiceImpl implements EhcacheService {


    @Override
    @Cacheable(value="sendMsgCache",key="'actaccountDateDtoCache[id-'+#id")
    public String find(String id) {
        return UUID.randomUUID().toString()+"---------------------new";
    }

    @Override
    @CachePut(value="sendMsgCache",key="'actaccountDateDtoCache[id-'+#id")
    public String inster(String id) {
        return UUID.randomUUID().toString();
    }


}
