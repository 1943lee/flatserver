package common;

import commoncache.ComCache;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hejiangbo on 2016/11/3.
 */
public class RedisUtil {

    private JedisPool _jedisPool;
    private static RedisUtil _redisUtil = new RedisUtil();
    private Logger logger = null;

    public static RedisUtil getInstance()
    {
        return _redisUtil;
    }

    public void subscribe(JedisPubSub jedisPubSub, String[] channels)
    {
        Jedis jedis = null;
        try
        {
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                JedisThread jedisThread = new JedisThread(jedis, jedisPubSub, channels, 0);
                jedisThread.setDaemon(true);
                jedisThread.start();
            }
        }
        catch (Exception e)
        {
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
    }

    public void psubscribe(JedisPubSub jedisPubSub, String[] patterns)
    {
        Jedis jedis = null;
        try
        {
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                JedisThread jedisThread = new JedisThread(jedis, jedisPubSub, patterns, 1);
                jedisThread.setDaemon(true);
                jedisThread.start();
            }
        }
        catch (Exception e)
        {
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
    }

    public void set(String key, String value)
    {
        Jedis jedis = null;
        try
        {
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                jedis.set(key, value);
            }
        }
        catch (Exception e)
        {
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (null != jedis)
                _jedisPool.returnResource(jedis);
        }
    }

    public String get(String key)
    {
        String result = "";
        Jedis jedis = null;
        try
        {
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                result = jedis.get(key);
            }
        }
        catch (Exception e)
        {
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (null != jedis)
                _jedisPool.returnResource(jedis);
        }

        return result;
    }

    public Set<String> keys(String pattern){
        Set<String> result = new HashSet<String>();
        Jedis jedis = null;
        try {
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                result = jedis.keys(pattern);
            }
        } catch (Exception e){
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (null != jedis)
                _jedisPool.returnResource(jedis);
        }
        return  result;
    }

    public String hget(String key, String field){
        String result = "";
        Jedis jedis = null;
        try	{
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                result = jedis.hget(key, field);
            }
        } catch (Exception e) {
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (null != jedis)
                _jedisPool.returnResource(jedis);
        }

        return result;
    }

    public void publish(String channel, String message)
    {
        Jedis jedis = null;
        try
        {
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                jedis.publish(channel, message);
            }
        }
        catch (Exception e)
        {
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (null != jedis)
                _jedisPool.returnResource(jedis);
        }
    }

    public boolean exists(String channel)
    {
        Jedis jedis = null;
        try
        {
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                return jedis.exists(channel);
            }
        }
        catch (Exception e)
        {
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return false;
        }
        finally
        {
            if (null != jedis)
                _jedisPool.returnResource(jedis);

            return false;
        }
    }

    public void delete(String channel)
    {
        Jedis jedis = null;
        try
        {
            if(_jedisPool != null)
            {
                jedis = _jedisPool.getResource();
                jedis.del(channel);
            }
        }
        catch (Exception e)
        {
            logger.error("redis连接超时！", e);
            if (null != jedis)
            {
                _jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (null != jedis)
                _jedisPool.returnResource(jedis);
        }
    }

    public boolean TestRedisLink()
    {
        if(_jedisPool == null)
            return false;

        try
        {
            Jedis jedis = _jedisPool.getResource();
            jedis.get("testKey");
            return true;
        }
        catch (Exception e)
        {
            logger.error("redis连接超时！", e);
            return false;
        }
    }

    private RedisUtil()
    {
        logger = Logger.getLogger(RedisUtil.class);
        initRedis();
    }

    private void initRedis()
    {
        String config =  ComCache.getInstance().getConfigCache().getPzxx("redis");

        if (!config.isEmpty())
        {
            if (config.length() > 0)
            {
                JedisPoolConfig jedisConfig = new JedisPoolConfig();
                jedisConfig.setMaxWaitMillis(1000 * 30);
                jedisConfig.setTestOnBorrow(true);
                jedisConfig.setMaxTotal(100);

                String[] ss = config.split(":");
                _jedisPool = new JedisPool(jedisConfig, ss[0], Integer.parseInt(ss[1]));
            }
            else
            {
                logger.info("系统配置表中，配置redis服务器地址为空！");
            }
        }
        else
        {
            logger.info("系统配置表中，没有配置redis服务器地址！");
        }
    }
}
