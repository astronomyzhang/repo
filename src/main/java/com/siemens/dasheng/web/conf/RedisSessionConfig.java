package com.siemens.dasheng.web.conf;

/**
 * yaming.chen@siemens.com
 * Created by chenyaming on 2017/7/17.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;

/**
 * @author Z003W5DZ
 * yaming.chen@siemens.com
 * Created by chenyaming on 2017/7/17.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600, redisNamespace = "dpp-session")
public class RedisSessionConfig {

    @Bean
    public CookieHttpSessionStrategy cookieHttpSessionStrategy() {
        CookieHttpSessionStrategy strategy = new CookieHttpSessionStrategy();
        DppCookieSerializer serializer = new DppCookieSerializer();
        serializer.setCookieMaxAge(7200);
        strategy.setCookieSerializer(serializer);
        return strategy;
    }

}
