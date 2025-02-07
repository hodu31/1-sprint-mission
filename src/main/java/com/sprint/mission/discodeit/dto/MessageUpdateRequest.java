package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class MessageUpdateRequest {
    private UUID messageId;
    private String content;
}
