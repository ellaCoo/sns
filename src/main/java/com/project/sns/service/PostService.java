package com.project.sns.service;

import com.project.sns.domain.Hashtag;
import com.project.sns.domain.Post;
import com.project.sns.domain.PostHashtag;
import com.project.sns.domain.UserAccount;
import com.project.sns.dto.*;
import com.project.sns.repository.PostHashtagRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {
    private final UserAccountRepository userAccountRepository;
    private final PostRepository postRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final HashtagService hashtagService;

    @Transactional(readOnly = true)
    public Page<PostWithLikesAndHashtagsDto> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostWithLikesAndHashtagsDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<PostWithLikesAndHashtagsDto> getPosts(UserAccountDto userAccountDto, Pageable pageable) {
        return postRepository.findByUserAccount_UserId(userAccountDto.userId(), pageable)
                .map(PostWithLikesAndHashtagsDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public PostWithLikesAndHashtagsAndCommentsDto getPostWithLikesAndHashtagsAndComments(Long postId) {
        return postRepository.findById(postId)
                .map(PostWithLikesAndHashtagsAndCommentsDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("포스트가 없습니다 - postId: " + postId));
    }

    @Transactional(readOnly = true)
    public PostWithLikesAndHashtagsDto getPost(Long postId) {
        return postRepository.findById(postId)
                .map(PostWithLikesAndHashtagsDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("포스트가 없습니다 - postId: " + postId));
    }

    public void updatePost(Long postId, PostWithHashtagsDto dto) {
        try {
            Post post = postRepository.getReferenceById(postId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            // 로그인된 사용자와 게시글 작성자가 동일한 경우(/postId/edit : get이므로 다른 사용자도 접근 가능)
            if (!post.getUserAccount().equals(userAccount)) {
                return;
            }
            if (dto.postDto().title() != null) post.setTitle(dto.postDto().title());
            if (dto.postDto().content() != null) post.setContent(dto.postDto().content());

            /**
             Hashtag LOGIC
             */
            Set<Long> originHashtagIds = post.getPostHashtags().stream().map(PostHashtag::getHashtag).map(Hashtag::getId).collect(Collectors.toSet());
            Set<String> newHashtags = dto.hashtagDtos().stream().map(HashtagDto::hashtagName).collect(Collectors.toUnmodifiableSet());

            postHashtagRepository.deleteByPostId(postId);

            Set<Hashtag> hashtags = hashtagService.getExistedOrCreatedHashtagsByHashtagNames(newHashtags);
            post.addHashtags(hashtags); // Post와 Hashtag 관계 설정
            postRepository.flush();

            hashtagService.deleteUnusedHashtags(originHashtagIds);
        } catch (EntityNotFoundException e) {
            log.warn("포스트 업데이트 실패. 포스트를 수정하는데 필요한 정보를 찾을 수 없습니다.");
        }
    }

    public void deletePost(Long postId, String userId) {
        // TODO: hashtag 기능 추가 시 함께 삭제 되도록
        postRepository.deleteByIdAndUserAccount_UserId(postId, userId);
    }

    public PostDto createPost(PostWithHashtagsDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

        Post post = dto.postDto().toEntity(userAccount);

        Set<String> newHashtags = dto.hashtagDtos().stream().map(HashtagDto::hashtagName).collect(Collectors.toUnmodifiableSet());
        Set<Hashtag> hashtags = hashtagService.getExistedOrCreatedHashtagsByHashtagNames(newHashtags);
        post.addHashtags(hashtags);

        post = postRepository.save(post);
        return PostDto.fromEntity(post);
    }
}
