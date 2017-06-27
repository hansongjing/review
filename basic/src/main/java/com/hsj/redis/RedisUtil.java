package com.hsj.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by hanhansongjiang on 17/6/19.
 */
public class RedisUtil {

    public static void set(String key) {

        Jedis jedis = JedisPoolUtil.getJedis();
        jedis.set(key, DateUtil.getCurrentDate());
        JedisPoolUtil.returnJedis(jedis);

    }

    public static String get(String key) {

        Jedis jedis = JedisPoolUtil.getJedis();
        String s = jedis.get(key);
        JedisPoolUtil.returnJedis(jedis);
        return s;

    }

    public static String getSet(String key, String date) {

        Jedis jedis = JedisPoolUtil.getJedis();
        String s = jedis.getSet(key, date);
        JedisPoolUtil.returnJedis(jedis);
        return s;


    }

    public static long setnx(String key,int expireTime) {
        Jedis jedis = JedisPoolUtil.getJedis();
        long ans = jedis.setnx(key, DateUtil.getCurrentDate());

        if (ans == 0) jedis.expire(key, expireTime);

        JedisPoolUtil.returnJedis(jedis);
        return ans;


    }

    public static long delete(String key) {
        Jedis jedis = JedisPoolUtil.getJedis();
        long ans = jedis.del(key);
        JedisPoolUtil.returnJedis(jedis);
        return ans;


    }
}
