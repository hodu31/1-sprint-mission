package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentService binaryContentService;

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    // 중복 체크
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with username " + username + " already exists");
    }

    // 프로필 이미지 저장
    UUID profileId = optionalProfileCreateRequest
        .map(binaryContentService::create)
        .map(BinaryContentDto::getId)
        .orElse(null);

    String password = userCreateRequest.password();

    // User 엔티티 생성 및 저장
    User user = new User(username, email, password, profileId);
    User createdUser = userRepository.save(user);

    // 사용자 상태(UserStatus) 생성 후 저장
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser, now);
    userStatusRepository.save(userStatus);

    return toDto(createdUser);
  }

  @Override
  @Transactional
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }

  @Override
  @Transactional
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    // 중복 체크
    if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    // 프로필 업데이트 요청이 있으면 기존 프로필 삭제 후 새 프로필 저장
    UUID newProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          if (user.getProfileId() != null) {
            binaryContentService.delete(user.getProfileId());
          }
          return binaryContentService.create(profileRequest).getId();
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, newProfileId);

    return toDto(userRepository.save(user));
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    // 프로필 이미지가 있으면 삭제
    if (user.getProfileId() != null) {
      binaryContentService.delete(user.getProfileId());
    }
    // 사용자 상태 삭제
    userStatusRepository.deleteByUser_Id(user.getId());
    // User 삭제
    userRepository.deleteById(userId);
  }

  private UserDto toDto(User user) {
    // UserStatusDto를 통해 online 상태 조회
    Boolean online = userStatusRepository.findByUser_Id(user.getId())
        .map(userStatus -> {
          UserStatusDto userStatusDto = new UserStatusDto(
              userStatus.getId(),
              userStatus.getUser().getId(),
              userStatus.getLastActiveAt()
          );
          Instant now = Instant.now();
          Instant lastActiveAt = userStatusDto.getLastActiveAt();
          return lastActiveAt != null && Duration.between(lastActiveAt, now).toMinutes() < 5;
        })
        .orElse(null);

    BinaryContentDto profileDto = (user.getProfileId() != null)
        ? binaryContentService.find(user.getProfileId())
        : null;

    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profileDto,
        online
    );
  }
}