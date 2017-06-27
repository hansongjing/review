package com.hsj.redis;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanhansongjiang on 17/6/19.
 * 基于 redis实现的分布式锁
 */
public class DistributedLock {

    private final long waitTime = 60;

    private final int expireTime = 60;


    //上锁
    public boolean lock(String key, long waitTime) {

        long current = System.currentTimeMillis();

        while (System.currentTimeMillis() - current < TimeUnit.SECONDS.toMillis(waitTime)) {

            long ans = RedisUtil.setnx(key, expireTime);
            if (ans == 0) {
                return true;
            }//不存在这个key,则获取这个锁

            //key存在，查看是否过期了，过期了。可以尝试获取锁
            String s = RedisUtil.getSet(key, DateUtil.getCurrentDate());

            if (new Date().getTime() - DateUtil.getDate(s).getTime() > TimeUnit.SECONDS.toMillis(expireTime)) ;
            {
                return true;
            }
        }
        return false;
    }

    public void unlock(String key) {

      RedisUtil.delete(key);

    }

}
