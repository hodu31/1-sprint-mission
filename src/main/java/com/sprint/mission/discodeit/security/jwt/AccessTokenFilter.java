package com.sprint.mission.discodeit.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class AccessTokenFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public AccessTokenFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      String jwt = token.substring(7);
      if (jwtService.isValidRefreshToken(jwt)) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(jwtService.getSecretKey())
            .build()
            .parseClaimsJws(jwt)
            .getBody();
        String username = ((Map<?, ?>) claims.get("userDto")).get("username").toString();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
      } else {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.startsWith("/api/auth/login") ||
        path.startsWith("/api/auth/register") ||
        path.startsWith("/api/auth/refresh") ||
        path.startsWith("/public");
  }
}
