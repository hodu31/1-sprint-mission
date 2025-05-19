package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // CSRF 설정
    CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    tokenRepository.setCookieName("CSRF-TOKEN");

    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    requestHandler.setCsrfRequestAttributeName("_csrf");

    http
        // LogoutFilter 제외
        .logout(AbstractHttpConfigurer::disable)
        // CSRF 설정
        .csrf(csrf -> csrf
            .csrfTokenRepository(tokenRepository)
            .csrfTokenRequestHandler(requestHandler)
        )
        // 요청 인증 설정
        .authorizeHttpRequests(authorize -> authorize
            // CSRF 토큰 발급 API는 인증 제외
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
            // 회원가입 API는 인증 제외
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            // /api/를 포함하지 않는 모든 URL은 인증 제외 (정적 리소스, swagger, actuator 등)
            .requestMatchers(new AntPathRequestMatcher("/**", "GET"),
                request -> !request.getRequestURI().contains("/api/"))
            .permitAll()
            // 그 외 모든 요청은 인증 필요
            .anyRequest().authenticated()
        );

    SecurityFilterChain filterChain = http.build();

    // 빌드 후에 필터 목록 로깅
    logFilterList(filterChain);

    return filterChain;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // 개발 환경에서 필터 목록 로깅을 위한 메서드
  @Profile("dev")
  private void logFilterList(SecurityFilterChain filterChain) {
    try {
      var filters = filterChain.getFilters();
      log.info("===== Security Filter List =====");
      for (int i = 0; i < filters.size(); i++) {
        log.info("{}: {}", i + 1, filters.get(i).getClass().getSimpleName());
      }
      log.info("===============================");
    } catch (Exception e) {
      log.error("필터 목록 로깅 중 오류 발생", e);
    }
  }
}
