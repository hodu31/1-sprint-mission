package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusResponse {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
    private Instant updatedAt;

    public ReadStatusResponse(ReadStatus readStatus) {
        this.id = readStatus.getId();
        this.userId = readStatus.getUser().getId();
        this.channelId = readStatus.getChannel().getId();
        this.lastReadAt = readStatus.getLastReadAt();
        this.updatedAt = readStatus.getUpdatedAt();
    }
}
