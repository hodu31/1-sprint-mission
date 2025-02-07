package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @InjectMocks
    private BasicMessageService basicMessageService;

    private User testUser;
    private Channel testChannel;
    private Message testMessage;
    private UUID testUserId;
    private UUID testChannelId;
    private UUID testMessageId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testChannelId = UUID.randomUUID();
        testMessageId = UUID.randomUUID();

        testUser = new User("testUser", "test@example.com", "password123");
        testChannel = new Channel(ChannelType.PUBLIC, "Test Channel", "Test Description");
        testMessage = new Message("Hello World!", testChannelId, testUserId);

    }

    @Test
    void createMessage_Success() {
        // Given
        MessageCreateRequest request = new MessageCreateRequest(testChannelId, testUserId, "Hello World!", null);
        when(channelRepository.findById(testChannelId)).thenReturn(Optional.of(testChannel));
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        // When
        MessageResponse response = basicMessageService.create(request);

        // Then
        assertNotNull(response);
        assertEquals("Hello World!", response.getContent());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void createMessage_Fail_ChannelNotFound() {
        // Given
        MessageCreateRequest request = new MessageCreateRequest(testChannelId, testUserId, "Hello World!", null);
        when(channelRepository.findById(testChannelId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> basicMessageService.create(request));
    }

    @Test
    void createMessage_Fail_UserNotFound() {
        // Given
        MessageCreateRequest request = new MessageCreateRequest(testChannelId, testUserId, "Hello World!", null);
        when(channelRepository.findById(testChannelId)).thenReturn(Optional.of(testChannel));
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> basicMessageService.create(request));
    }

    @Test
    void findAllMessagesByChannelId_Success() {
        // Given
        List<Message> messages = List.of(testMessage);
        when(messageRepository.findByChannelId(testChannelId)).thenReturn(messages);
        when(binaryContentRepository.findByMessage(any(Message.class))).thenReturn(Collections.emptyList());
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        List<MessageResponse> responses = basicMessageService.findAllByChannelId(testChannelId);

        // Then
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        verify(messageRepository, times(1)).findByChannelId(testChannelId);
    }

    @Test
    void updateMessage_Success() {
        // Given
        MessageUpdateRequest request = new MessageUpdateRequest(testMessageId, "Updated Content");
        when(messageRepository.findById(testMessageId)).thenReturn(Optional.of(testMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        // When
        MessageResponse response = basicMessageService.update(request);

        // Then
        assertEquals("Updated Content", response.getContent());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void updateMessage_Fail_NotFound() {
        // Given
        MessageUpdateRequest request = new MessageUpdateRequest(testMessageId, "Updated Content");
        when(messageRepository.findById(testMessageId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> basicMessageService.update(request));
    }

    @Test
    void deleteMessage_Success() {
        // Given
        when(messageRepository.findById(testMessageId)).thenReturn(Optional.of(testMessage));

        // When
        basicMessageService.delete(testMessageId);

        // Then
        verify(binaryContentRepository, times(1)).deleteByMessageId(testMessage);
        verify(messageRepository, times(1)).delete(testMessage);
    }

    @Test
    void deleteMessage_Fail_NotFound() {
        // Given
        when(messageRepository.findById(testMessageId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> basicMessageService.delete(testMessageId));
    }
}
