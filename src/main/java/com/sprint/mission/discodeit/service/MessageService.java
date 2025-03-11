package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);

  MessageDto find(UUID messageId);

  PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page, int size, String sort);

  MessageDto update(UUID messageId, MessageUpdateRequest request);

  void delete(UUID messageId);
}