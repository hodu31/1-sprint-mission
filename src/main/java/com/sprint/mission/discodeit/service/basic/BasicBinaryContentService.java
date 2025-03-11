package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContent savedContent = binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(savedContent.getId(), bytes);

    return new BinaryContentDto(
        savedContent.getId(),
        savedContent.getFileName(),
        savedContent.getSize(),
        savedContent.getContentType()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found"));
    return new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(binaryContentIds);
    return contents.stream()
        .map(content -> new BinaryContentDto(
            content.getId(),
            content.getFileName(),
            content.getSize(),
            content.getContentType()
        ))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
    }
    binaryContentStorage.delete(binaryContentId);
    binaryContentRepository.deleteById(binaryContentId);
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<?> download(UUID binaryContentId) {
    BinaryContentDto dto = find(binaryContentId);
    return binaryContentStorage.download(dto);
  }
}
