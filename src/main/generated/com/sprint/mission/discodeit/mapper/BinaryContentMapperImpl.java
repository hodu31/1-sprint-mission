package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-13T09:40:47+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class BinaryContentMapperImpl implements BinaryContentMapper {

    @Override
    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        BinaryContentDto binaryContentDto = new BinaryContentDto();

        binaryContentDto.setId( binaryContent.getId() );
        binaryContentDto.setFileName( binaryContent.getFileName() );
        binaryContentDto.setSize( binaryContent.getSize() );
        binaryContentDto.setContentType( binaryContent.getContentType() );

        return binaryContentDto;
    }
}
