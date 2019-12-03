package com.siemens.dasheng.web.util;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * redis lock
 *
 * @author ly
 * @data 2019.4.9
 */
public class RedisLock {


    /**
     * 加锁标志
     */
    private static final String LOCKED = "TRUE";
    /**
     * 毫秒与毫微秒的换算单位 1毫秒 = 1000000毫微秒
     */
    private static final long MILLI_NANO_CONVERSION = 1000 * 1000L;
    /**
     * 默认超时时间（毫秒）
     */
    private static final long DEFAULT_TIME_OUT = 1000;

    /**
     * 默认休眠时间（毫秒）
     */
    private static final long DEFAULT_TIME_SLEEP = 500;

    /**
     * 锁的超时时间（秒），过期删除
     */
    private static final int EXPIRE = 3 * 60;

    private StringRedisTemplate redisTemplate;

    private String key;

    private boolean locked = false;


    public RedisLock(String key, StringRedisTemplate redisTemplate) {
        this.key = key;
        this.redisTemplate = redisTemplate;
    }


    /**
     * 加锁
     * 默认加锁
     *
     * @return
     */
    public boolean lock() {
        return lock(DEFAULT_TIME_OUT);
    }

    /**
     * 加锁
     *
     * @param timeout
     * @return
     */
    public boolean lock(long timeout) {
        return lock(timeout, EXPIRE);
    }

    /**
     * 加锁
     *
     * @param timeout
     * @param expire
     * @return
     */
    public boolean lock(long timeout, int expire) {
        long nano = System.nanoTime();
        timeout *= MILLI_NANO_CONVERSION;
        if (addLock(timeout, nano, expire)) {
            this.locked = true;

        }
        return locked;
    }


    /**
     * 解锁
     */
    public void unlock() {
        try {
            if (this.locked) {
                this.redisTemplate.delete(this.key);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unlock error", e);
        }
    }

    /**
     * @param timeout 加锁超时时间
     * @param nano
     * @param expire  锁的超时时间(秒),过期删除
     * @return 成功或失败标志
     */
    private boolean addLock(long timeout, long nano, int expire) {
        try {
            while ((System.nanoTime() - nano) < timeout) {
                if (this.redisTemplate.opsForValue().setIfAbsent(this.key, LOCKED)) {
                    this.redisTemplate.expire(this.key, expire, TimeUnit.SECONDS);
                    return true;
                }
                // 短暂休眠，避免出现活锁
                Thread.sleep(DEFAULT_TIME_SLEEP);
            }
        } catch (Exception e) {
            throw new RuntimeException("Locking error", e);
        }
        return false;
    }


}
