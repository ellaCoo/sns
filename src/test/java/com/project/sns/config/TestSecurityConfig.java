package com.project.sns.config;

import com.project.sns.dto.UserAccountDto;
import com.project.sns.service.UserAccountService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean private UserAccountService userAccountService;

    @BeforeTestMethod
    public void securitySetUp() {
        // 각 테스트 실행 직전에 인증정보 넣어주라고 작성함
        given(userAccountService.searchUser(anyString()))
                .willReturn(Optional.of(createUserAccountDto()));
        given(userAccountService.saveUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .willReturn(createUserAccountDto());
    }

    private UserAccountDto createUserAccountDto() {
       return UserAccountDto.of(
               "ellaTest",
               "pw",
               "ella-test@mail.com",
               "ella-test",
               "test memmo"
       );
    }
}
