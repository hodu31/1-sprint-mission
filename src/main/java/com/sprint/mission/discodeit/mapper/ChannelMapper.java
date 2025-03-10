package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ChannelMapper {

  private final UserMapper userMapper;

  public ChannelMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public ChannelDto toDto(Channel channel) {
    ChannelDto dto = new ChannelDto();
    dto.setId(channel.getId());
    dto.setType(channel.getType());
    dto.setName(channel.getName());
    dto.setDescription(channel.getDescription());
    dto.setParticipants(channel.getMessages().stream()
        .map(message -> userMapper.toDto(message.getAuthor()))
        .distinct()
        .collect(Collectors.toList()));
    dto.setLastMessageAt(channel.getMessages().stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null));
    return dto;
  }
}