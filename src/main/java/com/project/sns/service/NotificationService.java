package com.project.sns.service;

import com.project.sns.domain.Post;
import com.project.sns.domain.UserAccount;
import com.project.sns.dto.NotificationDto;
import com.project.sns.dto.UserAccountDto;
import com.project.sns.repository.NotificationRepository;
import com.project.sns.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications(UserAccountDto userAccountDto) {
        String userId = userAccountDto.userId();
        List<NotificationDto> res = notificationRepository
                .findByUserIdSortingCreatedAtDesc(userId)
                .stream()
                .map(notification -> {
                    UserAccount occurUserAccount = userAccountRepository.getReferenceById(notification.getOccurUserId());
                    return NotificationDto.fromEntity(notification, occurUserAccount);
                })
                .collect(Collectors.toList());
        return res;
    }

    @Transactional(readOnly = true)
    public Long getPostIdById(Long id) {
        Post post = notificationRepository.findPostByNotificationId(id)
                .orElseThrow(() -> new EntityNotFoundException("알림에 해당하는 포스트를 찾을 수 없습니다 - notiId: " + id));
        return post.getId();
    }
}
