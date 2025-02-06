package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.nio.channels.Channel;

public interface UserStatusRepository {
    ReadStatus create(User user, Channel channel, ReadStatus readStatus);

}
