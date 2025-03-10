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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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

    // Channel 저장 후 DTO로 변환하여 반환
    Channel savedChannel = channelRepository.save(channel);
    return toDto(savedChannel);
  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    // 1) 채널 엔티티 생성 및 저장
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    // 2) participantIds()를 돌면서, 실제 User 엔티티를 조회한 후 ReadStatus 생성
    for (UUID userId : request.participantIds()) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

      ReadStatus status = new ReadStatus(user, createdChannel, Instant.EPOCH);
      readStatusRepository.save(status);
    }

    // DTO로 변환하여 반환
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
  @Transactional
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUser_Id(userId);
    List<UUID> mySubscribedChannelIds = readStatuses.stream()
        .map(rs -> rs.getChannel().getId())
        .distinct()
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getId())
        )
        .map(this::toDto)
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

    // Channel 저장 후 DTO로 변환하여 반환
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
    // 50개씩 최신 메시지 순으로 조회 (total count는 필요 없으므로 Slice 사용)
    Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"));
    Slice<Message> messageSlice = messageRepository.findAllByChannel_Id(channel.getId(), pageable);

    // 최신 메시지가 없으면 Instant.MIN, 있으면 첫번째 메시지의 생성일자를 사용
    Instant lastMessageAt = messageSlice.getContent().isEmpty()
        ? Instant.MIN
        : messageSlice.getContent().get(0).getCreatedAt();

    List<UserDto> participants;
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      participants = new ArrayList<>();
      readStatusRepository.findAllByChannel_Id(channel.getId())
          .stream()
          .map(ReadStatus::getUser)
          .distinct()
          .forEach(user -> participants.add(userMapper.toDto(user)));
    } else {
      participants = channel.getMessages().stream()
          .map(message -> userMapper.toDto(message.getAuthor()))
          .distinct()
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