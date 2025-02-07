package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class BinaryContentCreateRequest {
    private UUID userId;
    private UUID messageId;
    private MultipartFile file;
}
