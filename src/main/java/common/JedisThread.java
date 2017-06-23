package common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by hejiangbo on 2016/11/3.
 */
public class JedisThread extends Thread {

    private Jedis _jedis;
    private JedisPubSub _jedisPubSub;
    private String[] _channels;
    private int _type = 0;

    public JedisThread(Jedis jedis, JedisPubSub jedisPubSub, String[] channels, int type)
    {
        _jedis = jedis;
        _jedisPubSub = jedisPubSub;
        _channels = channels;
        _type = type;
    }

    public synchronized void run()
    {
        if(_type == 0)
        {
            _jedis.subscribe(_jedisPubSub, _channels);
        }
        else
        {
            _jedis.psubscribe(_jedisPubSub, _channels);
        }
    }
}
