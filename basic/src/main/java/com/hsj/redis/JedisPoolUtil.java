package com.hsj.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Optional;

/**
 * Created by hanhansongjiang on 17/6/19.
 */
public class JedisPoolUtil {

    private  static JedisPool jedisPool;


    public static JedisPool getPool(){

        JedisPoolConfig config = new JedisPoolConfig();
        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        config.setMaxIdle(500);
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(5);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(60*1000);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(config, "localhost", 6379);

        return jedisPool;

    }

    public static Jedis getJedis(){
        return jedisPool.getResource();

    }

    public static void returnJedis(Jedis jedis){
       jedisPool.returnBrokenResource(jedis);
    }





}
