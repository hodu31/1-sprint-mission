package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {


  @EntityGraph(attributePaths = {
      "author",
      "author.profile",
      "author.userStatus",
      "attachments"
  })
  Page<Message> findAllByChannel_Id(@Param("channelId") UUID channelId, Pageable pageable);

  void deleteAllByChannel_Id(UUID channelId);
}
