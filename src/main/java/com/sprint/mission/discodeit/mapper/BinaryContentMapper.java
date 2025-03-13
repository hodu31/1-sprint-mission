package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  // Entity에서 DTO로 변환
  BinaryContentDto toDto(BinaryContent binaryContent);

  // DTO에서 Entity로 변환 (필요한 경우)
  BinaryContent toEntity(BinaryContentDto dto);

  // Entity 리스트에서 DTO 리스트로 변환
  List<BinaryContentDto> toDtoList(List<BinaryContent> binaryContentList);
}