package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@RequiredArgsConstructor
@SpringBootApplication
public class DiscodeItApplication implements CommandLineRunner {

	private final BasicChannelService basicChannelService;
	private final BasicMessageService basicMessageService;
	private final UserRepository userRepository;

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeItApplication.class, args);
		DiscodeItApplication app = context.getBean(DiscodeItApplication.class);
		app.run();
	}

	@Override
	public void run(String... args) {
		System.out.println("=== DiscodeIt Application 시작 ===");

		try {
			// 1. 사용자 생성 및 저장
			User testUser = new User("testUser", "test@example.com", "password123");
			testUser = userRepository.save(testUser); // User 저장
			UUID testUserId = testUser.getId(); // 저장된 ID 가져오기

			System.out.println("사용자 생성 완료: " + testUser.getId());

			// 2. 공용 채널 생성
			PublicChannelCreateRequest publicChannelRequest = new PublicChannelCreateRequest("Public Channel", "A public chat room");
			ChannelResponse publicChannel = basicChannelService.createPublicChannel(publicChannelRequest);
			UUID testChannelId = publicChannel.getId();

			System.out.println("공용 채널 생성 완료: " + publicChannel.getId());

			// 3. 메시지 생성 (User가 DB에 저장된 상태에서)
			MessageCreateRequest messageRequest = new MessageCreateRequest(testChannelId, testUserId, "Hello World!", null);
			MessageResponse messageResponse = basicMessageService.create(messageRequest);

			System.out.println("메시지 생성 완료: " + messageResponse.getContent());


		} catch (Exception e) {
			System.err.println("애플리케이션 실행 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
