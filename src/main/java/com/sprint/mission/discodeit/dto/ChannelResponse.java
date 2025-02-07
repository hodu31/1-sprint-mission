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
    // user의 UUID 대신 user name을 반환 할까 고민했는데 username을 반환하면 단순히 채널을 만든뒤 반환 값은 보기 좋지만 나중에 UUID를 사용할 일이 생길것 같아 일단 UUID를 했습니다.
    //1번 user name도 반환하는 걸 만들어서 같이 반환한다
    //2번 user name 대신 uuid만 반환한다
    private List<UUID> participants;

    public ChannelResponse(Channel channel, Instant lastMessageTime, List<UUID> participants) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.type = channel.getType().name();
        this.lastMessageTime = lastMessageTime;
        this.participants = participants;
    }
}
