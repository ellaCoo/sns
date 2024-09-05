package com.project.sns.service;

import com.project.sns.domain.Hashtag;
import com.project.sns.repository.HashtagRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    public void deleteHashtagWithoutPosts(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);
        if (hashtag.getPostHashtags().isEmpty()) {
            hashtagRepository.delete(hashtag);
        }
    }
}
