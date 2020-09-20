package com.eplan.user.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Component
public class CacheUtility {

    @Autowired
    private RedisClient redisClient;

    public void delete(String prefix, String key){
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommand = connection.sync();
        String pair = prefix + ":" + key;
        syncCommand.del(pair);
        connection.close();
    }

    public void set(String prefix, String key, String value, Integer expiration){
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommand = connection.sync();
        String pair = prefix + ":" + key;
        syncCommand.set(pair, value);
        if(null != expiration){
            syncCommand.expire(pair, Long.valueOf(expiration));
        }
        connection.close();
    }

    public String get(String prefix, String key){
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommand = connection.sync();
        String pair = prefix + ":" + key;
        String value = syncCommand.get(pair);
        if(value == null){
            return null;
        }
        connection.close();
        return value;
    }
    
}
