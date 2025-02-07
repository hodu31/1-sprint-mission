package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    ReadStatus save(UserStatus userStatus);
    Optional<UserStatus> findByUser(User user);
    void deleteByUser(User user);
}
