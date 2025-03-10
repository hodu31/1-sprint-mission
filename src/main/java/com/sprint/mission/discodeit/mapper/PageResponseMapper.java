package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public class PageResponseMapper {

  public static <T> PageResponse<T> fromPage(Page<T> page) {
    PageResponse<T> response = new PageResponse<>();
    response.setContent(page.getContent());
    response.setNumber(page.getNumber());
    response.setSize(page.getSize());
    response.setHasNext(page.hasNext());
    response.setTotalElements(page.getTotalElements());
    return response;
  }

  public static <T> PageResponse<T> fromSlice(Slice<T> slice) {
    PageResponse<T> response = new PageResponse<>();
    response.setContent(slice.getContent());
    response.setNumber(slice.getNumber());
    response.setSize(slice.getSize());
    response.setHasNext(slice.hasNext());
    response.setTotalElements(null);
    return response;
  }
}
