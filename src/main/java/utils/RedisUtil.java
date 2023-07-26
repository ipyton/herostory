package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

// maintains a redis connection pool.
public class RedisUtil {
    static private Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    static private JedisPool _jedisPool = null;

    private RedisUtil(){}


    static public void init() {
        try {
            _jedisPool = new JedisPool("127.0.0.1", 6379);
            LOGGER.info("Redis connected successfully");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }
    static public Jedis getJedis(){
        if (null == _jedisPool) {
            throw new RuntimeException("redis haven't been init()");
        }
        Jedis jedis = _jedisPool.getResource();
        jedis.auth("123456");

        return jedis;
    }

}
