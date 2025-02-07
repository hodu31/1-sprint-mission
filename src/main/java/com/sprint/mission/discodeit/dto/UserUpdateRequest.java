package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UserUpdateRequest {
    private String username;
    private String email;
    private String password;
    private MultipartFile profileImage;
}
