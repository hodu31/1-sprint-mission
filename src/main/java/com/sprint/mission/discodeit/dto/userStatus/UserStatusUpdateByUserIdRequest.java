package com.sprint.mission.discodeit.dto.userStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusUpdateByUserIdRequest {
    private UUID userId;
    private Instant lastSeenAt;
}
