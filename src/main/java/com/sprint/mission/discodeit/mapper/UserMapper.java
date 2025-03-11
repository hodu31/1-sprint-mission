package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final UserStatusMapper userStatusMapper;

  public UserMapper(BinaryContentMapper binaryContentMapper, UserStatusMapper userStatusMapper) {
    this.binaryContentMapper = binaryContentMapper;
    this.userStatusMapper = userStatusMapper;
  }

  public UserDto toDto(User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() != null ? binaryContentMapper.toDto(user.getProfile()) : null,
        user.getUserStatus() != null && user.getUserStatus().isOnline()
    );
  }
}
