package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final User user; // 사용자 객체 참조 (프로필 이미지)
    private final Message message; // 메시지 객체 참조 (첨부 파일)
    private final byte[] data;
    private final String mimeType;

    public BinaryContent(User user, Message message, byte[] data, String mimeType) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.message = message;
        this.data = data;
        this.mimeType = mimeType;
    }
}
