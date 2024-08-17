package com.project.sns.service;

import com.project.sns.dto.PostDto;
import com.project.sns.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 포스트")
@ExtendWith(MockitoExtension.class) //Mockito를 사용하여 테스트를 작성할 때 필요한 확장 기능을 제공
public class PostServiceTest {
    @InjectMocks //PostService 객체를 생성하고, @Mock으로 주입된 PostRepository를 이 객체에 주입
    private PostService sut; //sut는 : "System Under Test", 테스트하고자 하는 실제 서비스 클래스의 인스턴스

    @Mock //PostRepository를 Mock 객체로 생성
    private PostRepository postRepository; //이 Mock 객체는 실제 데이터베이스나 외부 시스템과 상호작용하지 않고, 테스트 시에 정의된 동작만을 수행

    @DisplayName("포스트 페이지를 호출하면 페이징 처리하여 반환한다.")
    @Test
    void givenNoParameters_whenGetPosts_thenReturnsArticlePage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(postRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<PostDto> posts = sut.getPosts(pageable);

        // Then
        assertThat(posts).isEmpty();
        then(postRepository).should().findAll(pageable);
    }
}
