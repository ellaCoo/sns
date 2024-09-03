package com.project.sns.dto.response;

import com.project.sns.dto.PostCommentDto;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.UserAccountDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO - PostWithCommentsResponse")
@Disabled
class PostWithCommentsResponseTest {

    @DisplayName("자식 댓글이 없는 댓글 + 포스트 dto를 api 응답으로 변환할 때, 댓글을 시간 내림차순 + ID 오름차순으로 정렬한다.")
    @Test
    void givenPostWithCommentsDtoWithoutChildComments_whenMapping_thenOrganizesCommentsWithCertainOrder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<PostCommentDto> postCommentDtos = Set.of(
                createPostCommentDto(1L, null, now),
                createPostCommentDto(2L, null, now.plusDays(1L)),
                createPostCommentDto(3L, null, now.plusDays(3L)),
                createPostCommentDto(4L, null, now),
                createPostCommentDto(5L, null, now.plusDays(5L)),
                createPostCommentDto(6L, null, now.plusDays(4L)),
                createPostCommentDto(7L, null, now.plusDays(2L)),
                createPostCommentDto(8L, null, now.plusDays(7L))
        );
        PostDto postDto = createPostDto();

        // When
//        PostWithCommentsResponse actual = PostWithCommentsResponse.fromDto(postDto, postCommentDtos);
//
//        // Then
//        assertThat(actual.postCommentsResponse())
//                .containsExactly(
//                        createPostCommentResponse(8L, null, now.plusDays(7L)),
//                        createPostCommentResponse(5L, null, now.plusDays(5L)),
//                        createPostCommentResponse(6L, null, now.plusDays(4L)),
//                        createPostCommentResponse(3L, null, now.plusDays(3L)),
//                        createPostCommentResponse(7L, null, now.plusDays(2L)),
//                        createPostCommentResponse(2L, null, now.plusDays(1L)),
//                        createPostCommentResponse(1L, null, now),
//                        createPostCommentResponse(4L, null, now)
//                );
    }

    @DisplayName("포스트 + 댓글 dto를 api 응답으로 변환할 때, 댓글 부모 자식 관계를 각각의 규칙으로 정렬한다.")
    @Test
    void givenPostWithCommentsDto_whenMapping_thenOrganizesParentAndChildCommentsWithCertainOrder() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<PostCommentDto> postCommentDtos = Set.of(
                createPostCommentDto(1L, null, now),
                createPostCommentDto(2L, 1L, now.plusDays(1L)),
                createPostCommentDto(3L, 1L, now.plusDays(3L)),
                createPostCommentDto(4L, 1L, now),
                createPostCommentDto(5L, null, now.plusDays(5L)),
                createPostCommentDto(6L, null, now.plusDays(4L)),
                createPostCommentDto(7L, 6L, now.plusDays(2L)),
                createPostCommentDto(8L, 6L, now.plusDays(7L))
        );
        PostDto postDto = createPostDto();

        // When
//        PostWithCommentsResponse actual = PostWithCommentsResponse.fromDto(postDto, postCommentDtos);
//
//        // Then
//        assertThat(actual.postCommentsResponse())
//                .containsExactly(
//                        createPostCommentResponse(5L, null, now.plusDays(5L)),
//                        createPostCommentResponse(6L, null, now.plusDays(4L)),
//                        createPostCommentResponse(1L, null, now)
//                )
//                .flatExtracting(PostCommentResponse::childComments) // 컬렉션을 플랫(flat)하게 만든다
//                .containsExactly(
//                        createPostCommentResponse(7L, 6L, now.plusDays(2L)),
//                        createPostCommentResponse(8L, 6L, now.plusDays(7L)),
//                        createPostCommentResponse(4L, 1L, now),
//                        createPostCommentResponse(2L, 1L, now.plusDays(1L)),
//                        createPostCommentResponse(3L, 1L, now.plusDays(3L))
//                );
    }

    private PostDto createPostDto() {
        return PostDto.of(
                createUserAccountDto(),
                "title",
                "content"
        );
    }

    private PostCommentDto createPostCommentDto(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return PostCommentDto.of(
                id,
                1L,
                createUserAccountDto(),
                parentCommentId,
                "test comment :" + id,
                createdAt,
                "ella",
                createdAt,
                "ella"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "ella",
                "pw",
                "ella@mail.com",
                "ella",
                "memo"
        );
    }

    private PostCommentResponse createPostCommentResponse(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return PostCommentResponse.of(
                id,
                "test comment :" + id,
                createdAt,
                "ella@mail.com",
                "ella",
                "ella",
                parentCommentId
        );
    }
}