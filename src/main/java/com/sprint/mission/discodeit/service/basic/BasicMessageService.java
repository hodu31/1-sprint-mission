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

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));
    User author = userRepository.findById(authorId)
        .orElseThrow(
            () -> new NoSuchElementException("Author with id " + authorId + " does not exist"));

    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author, new ArrayList<>());
    message = messageRepository.save(message);

    for (BinaryContentCreateRequest attachmentRequest : binaryContentCreateRequests) {
      BinaryContentDto binaryContentDto = binaryContentService.create(attachmentRequest);

      MessageAttachment attachment = new MessageAttachment(message, binaryContentDto.getId());
      message.addAttachment(attachment);
    }

    Message savedMessage = messageRepository.save(message);
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
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page, int size,
      String sort) {
    String[] sortParams = sort.split(",");
    String sortField = sortParams[0];
    String sortDir = sortParams.length > 1 ? sortParams[1] : "asc";
    Sort.Direction direction = Sort.Direction.fromString(sortDir);
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

    Page<Message> messagePage = messageRepository.findAllByChannel_Id(channelId, pageable);

    List<UUID> attachmentIds = messagePage.stream()
        .flatMap(msg -> msg.getAttachments().stream().map(MessageAttachment::getAttachmentId))
        .distinct()
        .collect(Collectors.toList());

    List<BinaryContentDto> attachmentDtos = attachmentIds.isEmpty() ?
        new ArrayList<>() : binaryContentService.findAllByIdIn(attachmentIds);

    List<MessageDto> messageDtos = messagePage.getContent().stream().map(message -> {
      UserDto authorDto = new UserDto(
          message.getAuthor().getId(),
          message.getAuthor().getUsername(),
          message.getAuthor().getEmail(),
          message.getAuthor().getProfile() != null ?
              new BinaryContentDto(
                  message.getAuthor().getProfile().getId(),
                  message.getAuthor().getProfile().getFileName(),
                  message.getAuthor().getProfile().getSize(),
                  message.getAuthor().getProfile().getContentType()
              ) : null,
          null
      );

      List<BinaryContentDto> messageAttachments = attachmentDtos.stream()
          .filter(attachment -> message.getAttachments().stream()
              .anyMatch(msgAtt -> msgAtt.getAttachmentId().equals(attachment.getId())))
          .collect(Collectors.toList());

      return new MessageDto(
          message.getId(),
          message.getCreatedAt(),
          message.getUpdatedAt(),
          message.getContent(),
          message.getChannel().getId(),
          authorDto,
          messageAttachments
      );
    }).collect(Collectors.toList());

    PageResponse<MessageDto> response = new PageResponse<>();
    response.setContent(messageDtos);
    response.setNumber(messagePage.getNumber());
    response.setSize(messagePage.getSize());
    response.setHasNext(messagePage.hasNext());
    response.setTotalElements(messagePage.getTotalElements());

    return response;
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

    message.getAttachments().forEach(attachment -> {
      binaryContentService.delete(attachment.getAttachmentId());
    });

    messageRepository.deleteById(messageId);
  }

  private MessageDto toDto(Message message) {
    UserDto authorDto = new UserDto(
        message.getAuthor().getId(),
        message.getAuthor().getUsername(),
        message.getAuthor().getEmail(),
        null,
        null
    );

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