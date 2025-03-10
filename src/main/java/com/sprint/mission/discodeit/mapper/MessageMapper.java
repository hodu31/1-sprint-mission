package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MessageMapper {

  private final UserMapper userMapper;
  private final BinaryContentService binaryContentService;

  public MessageMapper(UserMapper userMapper, BinaryContentService binaryContentService) {
    this.userMapper = userMapper;
    this.binaryContentService = binaryContentService;
  }

  public MessageDto toDto(Message message) {
    MessageDto dto = new MessageDto();
    dto.setId(message.getId());
    dto.setCreatedAt(message.getCreatedAt());
    dto.setUpdatedAt(message.getUpdatedAt());
    dto.setContent(message.getContent());
    dto.setChannelId(message.getChannel() != null ? message.getChannel().getId() : null);
    dto.setAuthor(userMapper.toDto(message.getAuthor()));

    List<UUID> attachmentIds = message.getAttachments().stream()
        .map(attachment -> attachment.getAttachmentId())
        .collect(Collectors.toList());

    List<BinaryContentDto> attachmentDtos = attachmentIds.isEmpty()
        ? new ArrayList<>()
        : binaryContentService.findAllByIdIn(attachmentIds);

    dto.setAttachments(attachmentDtos);
    return dto;
  }
}