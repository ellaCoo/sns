package com.project.sns.service;

import com.project.sns.domain.UserAccount;
import com.project.sns.dto.UserAccountDto;
import com.project.sns.repository.UserAccountCacheRepository;
import com.project.sns.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final UserAccountCacheRepository userAccountCacheRepository;

    @Transactional(readOnly = true)
    public Optional<UserAccountDto> searchUser(String username) {
        return userAccountCacheRepository.getUserAccountCache(username)
                .or(() -> userAccountRepository.findById(username)
                        .map(userAccount -> {
                            userAccountCacheRepository.setUserAccountCache(userAccount);
                            return userAccount;
                        }))
                .map(UserAccountDto::fromEntity);
    }

    public UserAccountDto saveUser(String username, String password, String email, String nickname, String memo) {
        UserAccount userAccount = userAccountRepository.save(UserAccount.of(username, password, email, nickname, memo, username));
        userAccountCacheRepository.setUserAccountCache(userAccount);
        return UserAccountDto.fromEntity(userAccount);
    }
}
