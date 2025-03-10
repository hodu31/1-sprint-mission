package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

  public UserStatusDto toDto(UserStatus userStatus) {
    UserStatusDto dto = new UserStatusDto();
    dto.setId(userStatus.getId());
    dto.setUserId(userStatus.getUser() != null ? userStatus.getUser().getId() : null);
    dto.setLastActiveAt(userStatus.getLastActiveAt());
    return dto;
  }
}