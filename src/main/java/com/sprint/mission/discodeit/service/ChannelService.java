package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelResponse createPublicChannel(PublicChannelCreateRequest request);
    ChannelResponse find(UUID channelId);
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(ChannelUpdateRequest request);
    void delete(UUID channelId);
}
