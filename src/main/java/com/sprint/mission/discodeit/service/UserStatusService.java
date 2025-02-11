package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateByUserIdRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusCreateRequest request);
    UserStatusResponse findByUserId(UUID userId);
    List<UserStatusResponse> findAll(List<UUID> userIds);
    UserStatusResponse updateByUserId(UserStatusUpdateByUserIdRequest request);
    void deleteByUserId(UUID userId);
}
