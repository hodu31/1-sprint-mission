package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);
    ReadStatusResponse find(UUID id);
    List<ReadStatusResponse> findAllByUserId(UUID userId);
    ReadStatusResponse update(ReadStatusUpdateRequest request);
    void delete(UUID id);
}
