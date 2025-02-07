package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class MessageCreateRequest {
    private UUID channelId;
    private UUID authorId;
    private String content;
    private List<MultipartFile> attachments;
}
