package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

  public BinaryContentDto toDto(BinaryContent binaryContent) {
    BinaryContentDto dto = new BinaryContentDto();
    dto.setId(binaryContent.getId());
    dto.setFileName(binaryContent.getFileName());
    dto.setSize(binaryContent.getSize());
    dto.setContentType(binaryContent.getContentType());
    return dto;
  }
}

