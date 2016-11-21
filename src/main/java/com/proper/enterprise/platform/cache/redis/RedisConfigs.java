package com.proper.enterprise.platform.cache.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableCaching
@PropertySource("classpath:conf/cache/redis/cache-redis.properties")
public class RedisConfigs {

    @Value("${cache.redis.host}")
    private String hostName;

    @Value("${cache.redis.port}")
    private int port;

    @Value("${cache.redis.password}")
    private String password;

    @Value("${cache.redis.defaultExpiration}")
    private long defaultExpiration;

    @Value("${cache.redis.jedis.timeout}")
    private int timeout;

    @Value("${cache.redis.jedis.pool.maxIdle}")
    private int maxIdle;

    @Value("${cache.redis.jedis.pool.maxTotal}")
    private int maxTotal;

    @Resource(name = "isjRedisExpireProperties")
    private Properties expiresProperties;

    @Bean
    public RedisConnectionFactory jedisConnFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(hostName);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setTimeout(timeout);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        factory.setPoolConfig(poolConfig);
        return factory;
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(jedisConnFactory());
        return template;
    }

    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate());
        cacheManager.setDefaultExpiration(defaultExpiration);
        Map<String, Long> expires = new HashMap<>(expiresProperties.size());
        for (Map.Entry<Object, Object> entry : expiresProperties.entrySet()) {
            expires.put((String)entry.getKey(), Long.parseLong((String)entry.getValue()));
        }
        cacheManager.setExpires(expires);
        return cacheManager;
    }

}
