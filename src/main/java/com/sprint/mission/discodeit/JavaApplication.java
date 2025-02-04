package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.basic.*;
import com.sprint.mission.discodeit.service.*;

import java.util.*;

public class JavaApplication {
    public static void main(String[] args) {

        ChannelRepository jcfChannelRepo = new JCFChannelRepository();
        UserRepository jcfUserRepo = new JCFUserRepository();
        MessageRepository jcfMessageRepo = new JCFMessageRepository();

        ChannelRepository fileChannelRepo = new FileChannelRepository();
        UserRepository fileUserRepo = new FileUserRepository();
        MessageRepository fileMessageRepo = new FileMessageRepository();

        ChannelService jcfChannelService = new BasicChannelService(jcfChannelRepo);
        UserService jcfUserService = new BasicUserService(jcfUserRepo);
        MessageService jcfMessageService = new BasicMessageService(jcfMessageRepo);

        ChannelService fileChannelService = new BasicChannelService(fileChannelRepo);
        UserService fileUserService = new BasicUserService(fileUserRepo);
        MessageService fileMessageService = new BasicMessageService(fileMessageRepo);

        System.out.println("Testing JCF Repositories:");
        testServices(jcfChannelService, jcfUserService, jcfMessageService);

        System.out.println("\nTesting File Repositories:");
        testServices(fileChannelService, fileUserService, fileMessageService);
    }

    private static void testServices(ChannelService channelService, UserService userService, MessageService messageService) {

        User user = new User(UUID.randomUUID(), "woody", "woody@codeit.com", "woody1234");
        userService.createUser(user);

        Channel channel = new Channel(UUID.randomUUID(), ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        channelService.createChannel(channel);

        Message message = new Message(UUID.randomUUID(), "안녕하세요.", channel.getId(), user.getId());
        messageService.createMessage(message);

        System.out.println("All Users: " + userService.getAllUsers());

        System.out.println("All Channels: " + channelService.getAllChannels());

        System.out.println("All Messages: " + messageService.getAllMessages());
    }
}
