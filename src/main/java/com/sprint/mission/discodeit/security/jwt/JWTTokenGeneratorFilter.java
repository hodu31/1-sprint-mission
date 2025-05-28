package com.sprint.mission.discodeit.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
  @Value("${jwt.secret-key}")
  private String jwtSecret;
  @Value("${jwt.header}")
  private String jwtHeader;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (null != authentication) {
        String secret = jwtSecret;
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder().issuer("Eazy Bank").subject("JWT Token")
            .claim("username", authentication.getName())
            .claim("authorities", authentication.getAuthorities().stream().map(
                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + 30000000))
            .signWith(secretKey).compact();
        response.setHeader(jwtHeader, jwt);
      }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return !request.getServletPath().equals("/api/auth/login");
  }

}
