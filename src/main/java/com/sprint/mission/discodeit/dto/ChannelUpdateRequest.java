package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChannelUpdateRequest {
    private UUID id;
    private String name;
    private String description;
}
