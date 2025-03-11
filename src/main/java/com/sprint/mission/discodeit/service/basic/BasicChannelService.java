package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    Channel savedChannel = channelRepository.save(channel);
    return toDto(savedChannel);
  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    for (UUID userId : request.participantIds()) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

      ReadStatus status = new ReadStatus(user, createdChannel, Instant.EPOCH);
      readStatusRepository.save(status);
    }

    return toDto(createdChannel);
  }

  @Override
  @Transactional
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(this::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findAllByUserIdWithParticipants(userId);

    return channels.stream()
        .map(channel -> new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            channel.getReadStatuses().stream()
                .map(ReadStatus::getUser)
                .distinct()
                .map(userMapper::toDto)
                .collect(Collectors.toList()),
            null
        ))
        .collect(Collectors.toList());
  }


  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(newName, newDescription);

    Channel updatedChannel = channelRepository.save(channel);
    return toDto(updatedChannel);
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannel_Id(channel.getId());
    readStatusRepository.deleteAllByChannel_Id(channel.getId());

    channelRepository.deleteById(channelId);
  }

  private ChannelDto toDto(Channel channel) {
    Instant lastMessageAt = channel.getMessages().stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);

    List<UserDto> participants;
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      participants = channel.getReadStatuses().stream()
          .map(ReadStatus::getUser)
          .distinct()
          .map(userMapper::toDto)
          .collect(Collectors.toList());
    } else {
      participants = channel.getMessages().stream()
          .map(Message::getAuthor)
          .distinct()
          .map(userMapper::toDto)
          .collect(Collectors.toList());
    }

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );
  }

}