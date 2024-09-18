package com.project.sns.repository;

import com.project.sns.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserAccountCacheRepository {

    private final RedisTemplate<String, UserAccount> userAccountRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3); //redis공간을 효율적으로 사용하기 위해 3일동안만 저장

    public void setUserAccountCache(UserAccount userAccount) {
        log.info("Set User to Redis {}:{}", getRedisKey(userAccount.getUserId()), userAccount);
        userAccountRedisTemplate.opsForValue().set(getRedisKey(userAccount.getUserId()), userAccount, USER_CACHE_TTL);
    }

    public Optional<UserAccount> getUserAccountCache(String username) {
        String redisKey = getRedisKey(username);
        UserAccount userAccount = userAccountRedisTemplate.opsForValue().get(redisKey);
        log.info("Get User from Redis {}:{}", getRedisKey(username), userAccount);
        return Optional.ofNullable(userAccount);
    }

    private String getRedisKey(String username) {
        return "user:" + username;
    }
}
