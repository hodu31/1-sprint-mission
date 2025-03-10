package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  private final BinaryContentService binaryContentService;

  public UserMapper(BinaryContentService binaryContentService) {
    this.binaryContentService = binaryContentService;
  }

  public UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());

    dto.setProfile(user.getProfileId() != null
        ? binaryContentService.find(user.getProfileId())
        : null);

    dto.setOnline(user.getUserStatus() != null && user.getUserStatus().isOnline());
    return dto;
  }
}