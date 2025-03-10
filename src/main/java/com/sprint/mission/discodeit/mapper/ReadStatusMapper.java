package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  public ReadStatusDto toDto(ReadStatus readStatus) {
    ReadStatusDto dto = new ReadStatusDto();
    dto.setId(readStatus.getId());
    dto.setUserId(readStatus.getUser() != null ? readStatus.getUser().getId() : null);
    dto.setChannelId(readStatus.getChannel() != null ? readStatus.getChannel().getId() : null);
    dto.setLastReadAt(readStatus.getLastReadAt());
    return dto;
  }
}