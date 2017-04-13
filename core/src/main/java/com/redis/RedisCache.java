/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/4/11 0011
 */
package com.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * @author ZhangLinFeng
 * @name RedisCache
 * @data 2017/4/11 0011
 */
public class RedisCache  implements Cache{

    private Logger logger= LoggerFactory.getLogger(RedisCache.class);

    private RedisTemplate<String, Object> redisTemplate;

    private String name;

    private long keyTimeOut;


    /**
     * Return the cache name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Return the underlying native cache provider.
     */
    @Override
    public Object getNativeCache() {
        return null;
    }

    /**
     * Return the value to which this cache maps the specified key.
     * <p>Returns {@code null} if the cache contains no mapping for this key;
     * otherwise, the cached value (which may be {@code null} itself) will
     * be returned in a {@link ValueWrapper}.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which this cache maps the specified key,
     * contained within a {@link ValueWrapper} which may also hold
     * a cached {@code null} value. A straight {@code null} being
     * returned means that the cache contains no mapping for this key.
     * @see #get(Object, Class)
     */
    @Override
    public ValueWrapper get(Object key) {
        logger.debug("redis:get---[{}]",key);
        String keyf = key.toString();
        Object object ;
        try {
            object = redisTemplate.execute(new RedisCallback<Object>() {
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] key = keyf.getBytes();
                    byte[] value = connection.get(key);
                    if (value == null) {
                        return null;
                    }
                    return toObject(value);
                }
            });
        } catch (Exception e) {
            logger.error("redis get error ",e);
            return null;
        }
        return (object != null ? new SimpleValueWrapper(object) : null);
    }

    /**
     * Return the value to which this cache maps the specified key,
     * generically specifying a type that return value will be cast to.
     * <p>Note: This variant of {@code get} does not allow for differentiating
     * between a cached {@code null} value and no cache entry found at all.
     * Use the standard {@link #get(Object)} variant for that purpose instead.
     *
     * @param key  the key whose associated value is to be returned
     * @param type the required type of the returned value (may be
     *             {@code null} to bypass a type check; in case of a {@code null}
     *             value found in the cache, the specified type is irrelevant)
     * @return the value to which this cache maps the specified key
     * (which may be {@code null} itself), or also {@code null} if
     * the cache contains no mapping for this key
     * @throws IllegalStateException if a cache entry has been found
     *                               but failed to match the specified type
     * @see #get(Object)
     * @since 4.0
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        logger.debug("redis:get type---[{}]",key);
        String keyf = key.toString();
        T object ;
        try {
            object = redisTemplate.execute(new RedisCallback<T>() {
                public T doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] key = keyf.getBytes();
                    byte[] value = connection.get(key);
                    if (value == null) {
                        return null;
                    }
                    return type.cast(toObject(value));
                }
            });
        } catch (Exception e) {
            logger.error("redis get error ",e);
            return null;
        }
        return object;
    }

    /**
     * Return the value to which this cache maps the specified key, obtaining
     * that value from {@code valueLoader} if necessary. This method provides
     * a simple substitute for the conventional "if cached, return; otherwise
     * create, cache and return" pattern.
     * <p>If possible, implementations should ensure that the loading operation
     * is synchronized so that the specified {@code valueLoader} is only called
     * once in case of concurrent access on the same key.
     * <p>If the {@code valueLoader} throws an exception, it is wrapped in
     * a {@link ValueRetrievalException}
     *
     * @param key         the key whose associated value is to be returned
     * @param valueLoader
     * @return the value to which this cache maps the specified key
     * @throws ValueRetrievalException if the {@code valueLoader} throws an exception
     * @since 4.3
     */
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    /**
     * Associate the specified value with the specified key in this cache.
     * <p>If the cache previously contained a mapping for this key, the old
     * value is replaced by the specified value.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    @Override
    public void put(Object key, Object value) {
        logger.debug("redis:put---key:[{}],value:[{}]",key,value);
        try{
            redisTemplate.execute((RedisCallback<Long>) connection -> {
                byte[] keys=key.toString().getBytes();
                byte[] values=toByteArray(value);
                connection.set(keys,values);
                //如果设置了超期时间，那么更新该key的超期时间
                if(keyTimeOut>0){
                    connection.expire(keys,keyTimeOut);
                }
                return 1L;
            });
        }catch(Exception e){
            logger.error("redis:put---key:[{}],value:[{}]!error",key,value,e);
        }

    }

    /**
     * Atomically associate the specified value with the specified key in this cache
     * if it is not set already.
     * <p>This is equivalent to:
     * <pre><code>
     * Object existingValue = cache.get(key);
     * if (existingValue == null) {
     *     cache.put(key, value);
     *     return null;
     * } else {
     *     return existingValue;
     * }
     * </code></pre>
     * except that the action is performed atomically. While all out-of-the-box
     * {@link CacheManager} implementations are able to perform the put atomically,
     * the operation may also be implemented in two steps, e.g. with a check for
     * presence and a subsequent put, in a non-atomic way. Check the documentation
     * of the native cache implementation that you are using for more details.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the value to which this cache maps the specified key (which may be
     * {@code null} itself), or also {@code null} if the cache did not contain any
     * mapping for that key prior to this call. Returning {@code null} is therefore
     * an indicator that the given {@code value} has been associated with the key.
     * @since 4.1
     */
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    /**
     * Evict the mapping for this key from this cache if it is present.
     *
     * @param key the key whose mapping is to be removed from the cache
     */
    @Override
    public void evict(Object key) {
        logger.info("redis:del key:[{}]",key);
        try{
            redisTemplate.execute((RedisCallback<Long>) connection ->{
                byte[] keys=key.toString().getBytes();
                return connection.del(keys);
            });
        }catch(Exception e){
            logger.error("redis:del key:[{}] error",key,e);
        }

    }

    /**
     * Remove all mappings from the cache.
     */
    @Override
    public void clear() {
        logger.info("redis:clear ");
        try{
            redisTemplate.execute((RedisCallback<Long>) connection ->{
                 connection.flushDb();
                 return 1L;
            });
        }catch(Exception e){
            logger.error("redis:clear error !",e);
        }
    }


    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getKeyTimeOut() {
        return keyTimeOut;
    }

    public void setKeyTimeOut(long keyTimeOut) {
        this.keyTimeOut = keyTimeOut;
    }


    private byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (Exception ex) {
            logger.error("redis:toByteArray error!",ex);
        }
        return bytes;
    }

    private Object toObject(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            ois.close();
            bis.close();
            return obj;
        } catch (Exception ex) {
            logger.error("redis:toObject error!",ex);
            return null;
        }

    }
}
