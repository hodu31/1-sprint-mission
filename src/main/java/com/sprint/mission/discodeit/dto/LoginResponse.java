package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;

import java.util.UUID;

@Getter
public class LoginResponse {
    private UUID id;
    private String username;
    private String email;

    public LoginResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
