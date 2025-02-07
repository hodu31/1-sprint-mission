package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class ChannelResponse {
    private UUID id;
    private String name;
    private String type;
    private Instant lastMessageTime;
    private List<UUID> participants;

    public ChannelResponse(Channel channel, Instant lastMessageTime, List<UUID> participants) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.type = channel.getType().name();
        this.lastMessageTime = lastMessageTime;
        this.participants = participants;
    }
}
