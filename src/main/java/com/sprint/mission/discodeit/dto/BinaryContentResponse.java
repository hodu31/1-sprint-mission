package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContentResponse {
    private UUID id;
    private UUID userId;
    private UUID messageId;
    private String mimeType;
    private long fileSize;

    public BinaryContentResponse(BinaryContent binaryContent) {
        this.id = binaryContent.getId();
        this.userId = binaryContent.getUser().getId();
        this.messageId = binaryContent.getMessage().getId();
        this.mimeType = binaryContent.getMimeType();
        this.fileSize = binaryContent.getData().length;
    }
}
