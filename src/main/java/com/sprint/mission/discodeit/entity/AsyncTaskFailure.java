package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AsyncTaskFailure extends BaseEntity {

  @Column(nullable = false)
  private String taskName;

  @Column(nullable = false)
  private String requestId;

  @Column(nullable = false)
  private String failureReason;

  public AsyncTaskFailure(String taskName, String requestId, String failureReason, String stackTrace) {
    this.taskName = taskName;
    this.requestId = requestId;
    this.failureReason = failureReason;
  }
}