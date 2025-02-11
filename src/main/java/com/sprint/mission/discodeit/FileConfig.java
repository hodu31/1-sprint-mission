package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileConfig {

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new FileBinaryContentRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository();
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new FileReadStatusRepository();
    }

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return new FileUserStatusRepository();
    }
}
