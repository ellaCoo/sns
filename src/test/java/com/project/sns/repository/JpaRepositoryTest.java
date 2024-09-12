package com.project.sns.repository;

import com.project.sns.config.JpaConfig;
import com.project.sns.domain.Hashtag;
import com.project.sns.domain.Post;
import com.project.sns.domain.PostHashtag;
import com.project.sns.domain.UserAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.engine.spi.SessionLazyDelegator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
/*
단위 테스트에서는 spring 컨테이너를 사용하면 안된다.
@DataJpaTest: repository 객체를 의존주입 받을 수 있게 해준다. | @Transactional 을 갖고있어서 테스트 이후 자동 롤백 가능
 */
@DisplayName("JPA 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DataJpaTest
public class JpaRepositoryTest {

    @Autowired private PostRepository postRepository;
    @Autowired private PostCommentRepository postCommentRepository;
    @Autowired private HashtagRepository hashtagRepository;
    @Autowired private LikeRepository likeRepository;
    @Autowired private PostHashtagRepository postHashtagRepository;
    @Autowired private UserAccountRepository userAccountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // Given

        // When
        List<Post> posts = postRepository.findAll();

        // Then
        assertThat(posts)
                .isNotNull()
                .hasSize(65);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        // Given
        long previousCnt = postRepository.count();
        String username = "ella";
        UserAccount userAccount = userAccountRepository.findById(username).orElseThrow();
        Post post = Post.of(userAccount, "new post", "new content");
        // TODO: 개발 진행 단계에 따라 테스트 추가 예정 (해시태그)

        // When
        postRepository.save(post);

        // Then
        assertThat(postRepository.count()).isEqualTo(previousCnt + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // Given
        Post post = postRepository.findById(1L).orElseThrow();
        String updatedTitle = "updatedTitle";
        post.setTitle(updatedTitle);
        // TODO: 개발 진행 단계에 따라 테스트 추가 예정 (해시태그)

        // When
        postRepository.saveAndFlush(post);
        /**
         * 업데이트에서 영속성 컨텍스트로부터 가져온 데이터를 그냥 save하고 추가적인 작업하지 않고 끝내버리면
         * 어차피 롤백(@DataJpaTest)할거라서 save하고 flush(영속성 컨텍스트의 변경내용을 DB에 동기화) 해줘야 한다.
         */
        entityManager.clear();

        // Then
        Post updatedPost = postRepository.findById(1L).orElseThrow();
        assertThat(updatedPost).hasFieldOrPropertyWithValue("title", updatedTitle);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        // Given
        Post post = postRepository.findById(1L).orElseThrow();
        long previousPostCnt = postRepository.count();
        long previousPostCommentCnt = postCommentRepository.count();
        int deletedCommentsSize = post.getPostComments().size();

        // When
        postRepository.delete(post);

        // Then
        assertThat(postRepository.count())
                .isEqualTo(previousPostCnt - 1);
        assertThat(postCommentRepository.count())
                .isEqualTo(previousPostCommentCnt - deletedCommentsSize);
    }

    // insert test시 auditing 무시하도록
    @EnableJpaAuditing
    @TestConfiguration // 빈으로 등록하되, 테스트할때만 빈으로 등록해라
    // jpa에서 오디팅에서 security로부터의 영향에서 자유로워질 수 있게 따로 auditorAware를 테스트 전용 config로 등록해서 사용
    static class TestJpaConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of("ella");
        }
    }
}
