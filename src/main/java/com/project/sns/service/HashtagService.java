package com.project.sns.service;

import com.project.sns.domain.Hashtag;
import com.project.sns.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public void deleteUnusedHashtags(Set<Long> hashtagIds) {
        // JPQL 쿼리 실행 전, 자동 flush
        List<Hashtag> unusedHashtags = hashtagRepository.findUnusedHashtagsByIds(hashtagIds);
        for (Hashtag unusedHashtag : unusedHashtags) {
            hashtagRepository.delete(unusedHashtag);
        }
    }

    public Set<Hashtag> getExistedOrCreatedHashtagsByHashtagNames(Set<String> hashtagNAmes) {
        Set<Hashtag> hashtags = new HashSet<>();
        for (String hashtagName : hashtagNAmes) {
            Hashtag hashtag = hashtagRepository.findByHashtagName(hashtagName)
                    .orElseGet(() -> hashtagRepository.save(Hashtag.of(hashtagName)));
            hashtags.add(hashtag);
        }
        return hashtags;
    }
}
