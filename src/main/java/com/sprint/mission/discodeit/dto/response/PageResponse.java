package com.sprint.mission.discodeit.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> {

  private List<T> content;

  private int number;

  private int size;

  private boolean hasNext;

  private Long totalElements;
}
