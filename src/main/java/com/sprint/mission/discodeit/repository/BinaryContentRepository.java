package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;

import java.util.Optional;

public interface BinaryContentRepository {
    Optional<BinaryContent> findByUser(User user);

    void deleteByUser(User user);
    BinaryContent save(BinaryContent binaryContent);
}
