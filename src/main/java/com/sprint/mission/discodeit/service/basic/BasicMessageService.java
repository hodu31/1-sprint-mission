package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    // 실제 Channel, User 엔티티 조회
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));
    User author = userRepository.findById(authorId)
        .orElseThrow(
            () -> new NoSuchElementException("Author with id " + authorId + " does not exist"));

    String content = messageCreateRequest.content();
    // 첨부파일은 아직 없으므로 빈 리스트로 생성
    Message message = new Message(content, channel, author, new ArrayList<>());
    // 초기 Message 저장 (필요시 ID 생성)
    message = messageRepository.save(message);

    // 첨부파일 처리: BinaryContentService를 통해 BinaryContent 생성 및 저장
    for (BinaryContentCreateRequest attachmentRequest : binaryContentCreateRequests) {
      BinaryContentDto binaryContentDto = binaryContentService.create(attachmentRequest);

      // MessageAttachment 생성 및 Message에 추가
      MessageAttachment attachment = new MessageAttachment(message, binaryContentDto.getId());
      message.addAttachment(attachment);
    }

    // 첨부파일이 반영된 Message를 다시 저장
    Message savedMessage = messageRepository.save(message);
    // MessageDto로 변환하여 반환
    return toDto(savedMessage);
  }

  @Override
  @Transactional
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    return toDto(message);
  }

  @Override
  @Transactional
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Message> messagePage = messageRepository.findAllByChannel_Id(channelId, pageable);

    // Page<Message> -> Page<MessageDto> (map 연산)
    Page<MessageDto> dtoPage = messagePage.map(this::toDto);

    // PageResponseMapper를 통해 Page<MessageDto> -> PageResponse<MessageDto>
    return PageResponseMapper.fromPage(dtoPage);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(newContent);
    Message updatedMessage = messageRepository.save(message);
    return toDto(updatedMessage);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    // 첨부파일(MessageAttachment)에 포함된 BinaryContent 삭제
    message.getAttachments().forEach(attachment -> {
      binaryContentService.delete(attachment.getAttachmentId());
    });

    messageRepository.deleteById(messageId);
  }

  // Message를 MessageDto로 변환하는 메서드
  private MessageDto toDto(Message message) {
    UserDto authorDto = new UserDto(
        message.getAuthor().getId(),
        message.getAuthor().getUsername(),
        message.getAuthor().getEmail(),
        null,
        null
    );

    // BinaryContentService를 통해 첨부파일 정보 조회
    List<BinaryContentDto> attachmentDtos = message.getAttachments().stream()
        .map(attachment -> binaryContentService.find(attachment.getAttachmentId()))
        .collect(Collectors.toList());

    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        authorDto,
        attachmentDtos
    );
  }
}