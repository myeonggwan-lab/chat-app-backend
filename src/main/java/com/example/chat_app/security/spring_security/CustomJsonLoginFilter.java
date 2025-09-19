package com.example.chat_app.security.spring_security;
import com.example.chat_app.service.RefreshTokenService;
import com.example.chat_app.enums.Role;
import com.example.chat_app.dto.LoginMemberDto;
import com.example.chat_app.dto.MemberDto;
import com.example.chat_app.security.jwt.JwtUtil;
import com.example.chat_app.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import java.io.IOException;

import static com.example.chat_app.config.ConstantConfig.ACCESS_EXPIRED_MS;
import static com.example.chat_app.config.ConstantConfig.COOKIE_MAX_AGE;


public class CustomJsonLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final String LOGIN_URL = "/auth/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenService refreshTokenService;

    public CustomJsonLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, ObjectMapper objectMapper,
                                 RefreshTokenService refreshTokenService) {
        // RequestMatcher 람다로 교체: POST /login 요청만 필터링
        super(request -> LOGIN_URL.equals(request.getRequestURI())
                && HTTP_METHOD.equalsIgnoreCase(request.getMethod()));

        setAuthenticationManager(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if(request.getContentType() == null || !request.getContentType().toLowerCase().contains("application/json")) {
            throw new HttpMediaTypeNotSupportedException("Unsupported content type: " + request.getContentType());
        }

        LoginMemberDto loginMemberDto = objectMapper.readValue(request.getInputStream(), LoginMemberDto.class);

        if(!StringUtils.hasText(loginMemberDto.getLoginId()) || !StringUtils.hasText(loginMemberDto.getPassword())) {
            throw new BadCredentialsException("LoginId or Password is missing");
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginMemberDto.getLoginId(), loginMemberDto.getPassword());

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails)authResult.getPrincipal();
        String loginId = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.createJwt("access", loginId, role, ACCESS_EXPIRED_MS);

        String key = "refresh:"+loginId;
        String refreshToken = refreshTokenService.createRefreshToken(key, loginId, role);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Set-Cookie", createCookie(refreshToken).toString());

        MemberDto data = MemberDto.builder().loginId(loginId).role(Role.valueOf(role)).build();
        ResponseUtils.sendSuccessResponse(response, "로그인 성공", data, HttpServletResponse.SC_OK);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ResponseUtils.sendFailResponse(response, "UNAUTHORIZED", "로그인 실패", HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * 쿠키 생성 메서드
     */
    private ResponseCookie createCookie(String value) {
        return  ResponseCookie.from("refresh", value)
                .httpOnly(true)
                .secure(false)        // 로컬 테스트
                .sameSite("Lax")     // 크로스도메인 허용
                .path("/")
                .maxAge(COOKIE_MAX_AGE)
                .build();
    }
}
