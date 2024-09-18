package com.project.sns.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.sns.domain.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

    private static final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Bean // redis에 접속하기 위한 설정
    RedisConnectionFactory redisConnectionFactory(
            @Value("${redis.host}") String redisHost,
            @Value("${redis.port}") int redisPort
    ) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        return new LettuceConnectionFactory(config); // lettuce : 최신, 성능 좋다고 알려진 구현체
    }

    @Bean
    public RedisTemplate<String, UserAccount> userAccountRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, UserAccount> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer()); //문자열로 변환하여 키값 세팅
        //value: 데이터가 복잡.. 필드 데이터도 있고 -> Json형태의 문자열로
        redisTemplate.setValueSerializer(
                new Jackson2JsonRedisSerializer<>(objectMapper, UserAccount.class));

        return redisTemplate;
    }
}
