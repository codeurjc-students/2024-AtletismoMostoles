package com.example.TFG_WebApp.SECURITY_TESTs;

import com.example.TFG_WebApp.Security.JWT.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtCookieManager cookieUtil;

    @InjectMocks
    private UserLoginService loginService;

    private LoginRequest loginRequest;
    private UserDetails mockUser;
    private Token newAccess;
    private Token newRefresh;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("user1", "pass1");
        mockUser = User.withUsername("user1").password("pass1").roles("USER").build();
        LocalDateTime now = LocalDateTime.now();
        newAccess = new Token(Token.TokenType.ACCESS, "accToken", 3600L, now.plusSeconds(3600));
        newRefresh = new Token(Token.TokenType.REFRESH, "refToken", 7200L, now.plusSeconds(7200));
    }

    @Test
    void login_bothTokensInvalid_generatesBothCookies() {
        try (MockedStatic<SecurityCipher> sc = mockStatic(SecurityCipher.class)) {
            sc.when(() -> SecurityCipher.decrypt("encAcc")).thenReturn("decAcc");
            sc.when(() -> SecurityCipher.decrypt("encRef")).thenReturn("decRef");

            Authentication auth = new UsernamePasswordAuthenticationToken("user1", "pass1");
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(userDetailsService.loadUserByUsername("user1")).thenReturn(mockUser);

            when(jwtTokenProvider.validateToken("decAcc")).thenReturn(false);
            when(jwtTokenProvider.validateToken("decRef")).thenReturn(false);
            when(jwtTokenProvider.generateToken(mockUser)).thenReturn(newAccess);
            when(jwtTokenProvider.generateRefreshToken(mockUser)).thenReturn(newRefresh);

            ResponseCookie accCookie = ResponseCookie.from(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME, "encAcc2").build();
            ResponseCookie refCookie = ResponseCookie.from(JwtCookieManager.REFRESH_TOKEN_COOKIE_NAME, "encRef2").build();
            when(cookieUtil.createAccessTokenCookie("accToken")).thenReturn(accCookie);
            when(cookieUtil.createRefreshTokenCookie("refToken")).thenReturn(refCookie);

            ResponseEntity<AuthResponse> response = loginService.login(loginRequest, "encAcc", "encRef");

            assertThat(response.getStatusCodeValue()).isEqualTo(200);
            List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            assertThat(setCookies).containsExactly(accCookie.toString(), refCookie.toString());
            assertThat(response.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);

            sc.verify(() -> SecurityCipher.decrypt(anyString()), times(2));
        }
    }

    @Test
    void login_accessInvalid_refreshValid_generatesAccessCookieOnly() {
        try (MockedStatic<SecurityCipher> sc = mockStatic(SecurityCipher.class)) {
            sc.when(() -> SecurityCipher.decrypt("encAcc")).thenReturn("decAcc");
            sc.when(() -> SecurityCipher.decrypt("encRef")).thenReturn("decRef");

            Authentication auth = new UsernamePasswordAuthenticationToken("user1", "pass1");
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(userDetailsService.loadUserByUsername("user1")).thenReturn(mockUser);

            when(jwtTokenProvider.validateToken("decAcc")).thenReturn(false);
            when(jwtTokenProvider.validateToken("decRef")).thenReturn(true);
            when(jwtTokenProvider.generateToken(mockUser)).thenReturn(newAccess);

            ResponseCookie accCookie = ResponseCookie.from(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME, "encAcc2").build();
            when(cookieUtil.createAccessTokenCookie("accToken")).thenReturn(accCookie);

            ResponseEntity<AuthResponse> response = loginService.login(loginRequest, "encAcc", "encRef");
            List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            assertThat(setCookies).containsExactly(accCookie.toString());
        }
    }

    @Test
    void login_bothTokensValid_generatesBothCookies() {
        try (MockedStatic<SecurityCipher> sc = mockStatic(SecurityCipher.class)) {
            sc.when(() -> SecurityCipher.decrypt("encAcc")).thenReturn("decAcc");
            sc.when(() -> SecurityCipher.decrypt("encRef")).thenReturn("decRef");

            Authentication auth = new UsernamePasswordAuthenticationToken("user1", "pass1");
            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(userDetailsService.loadUserByUsername("user1")).thenReturn(mockUser);

            when(jwtTokenProvider.validateToken("decAcc")).thenReturn(true);
            when(jwtTokenProvider.validateToken("decRef")).thenReturn(true);
            when(jwtTokenProvider.generateToken(mockUser)).thenReturn(newAccess);
            when(jwtTokenProvider.generateRefreshToken(mockUser)).thenReturn(newRefresh);

            ResponseCookie accCookie = ResponseCookie.from(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME, "encAcc2").build();
            ResponseCookie refCookie = ResponseCookie.from(JwtCookieManager.REFRESH_TOKEN_COOKIE_NAME, "encRef2").build();
            when(cookieUtil.createAccessTokenCookie("accToken")).thenReturn(accCookie);
            when(cookieUtil.createRefreshTokenCookie("refToken")).thenReturn(refCookie);

            ResponseEntity<AuthResponse> response = loginService.login(loginRequest, "encAcc", "encRef");
            List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            assertThat(setCookies).containsExactly(accCookie.toString(), refCookie.toString());
        }
    }

    @Test
    void refresh_tokenInvalid_returnsFailure() {
        try (MockedStatic<SecurityCipher> sc = mockStatic(SecurityCipher.class)) {
            sc.when(() -> SecurityCipher.decrypt("encRef")).thenReturn("decRef");
            when(jwtTokenProvider.validateToken("decRef")).thenReturn(false);

            ResponseEntity<AuthResponse> response = loginService.refresh("encRef");
            assertThat(response.getBody().getStatus()).isEqualTo(AuthResponse.Status.FAILURE);
            assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNullOrEmpty();
        }
    }

    @Test
    void refresh_tokenValid_generatesAccessCookie() {
        try (MockedStatic<SecurityCipher> sc = mockStatic(SecurityCipher.class)) {
            sc.when(() -> SecurityCipher.decrypt("encRef")).thenReturn("decRef");
            when(jwtTokenProvider.validateToken("decRef")).thenReturn(true);
            when(jwtTokenProvider.getUsername("decRef")).thenReturn("user1");
            when(userDetailsService.loadUserByUsername("user1")).thenReturn(mockUser);
            when(jwtTokenProvider.generateToken(mockUser)).thenReturn(newAccess);
            ResponseCookie accCookie = ResponseCookie.from(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME, "encAcc2").build();
            when(cookieUtil.createAccessTokenCookie("accToken")).thenReturn(accCookie);

            ResponseEntity<AuthResponse> response = loginService.refresh("encRef");
            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            assertThat(cookies).containsExactly(accCookie.toString());
            assertThat(response.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        }
    }

    @Test
    void getUserName_returnsAuthenticationName() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("userX", null)
        );
        assertThat(loginService.getUserName()).isEqualTo("userX");
    }

    @Test
    void logout_invalidatesSession_andClearsCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession(true);
        Cookie cookie = new Cookie(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME, "val");
        request.setCookies(cookie);

        String result = loginService.logout(request, response);
        assertThat(result).isEqualTo("logout successfully");
        assertThat(request.getSession(false)).isNull();
        jakarta.servlet.http.Cookie[] respCookies = response.getCookies();
        assertThat(respCookies).hasSize(1);
        jakarta.servlet.http.Cookie c = respCookies[0];
        assertThat(c.getName()).isEqualTo(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME);
        assertThat(c.getMaxAge()).isEqualTo(0);
        assertThat(c.getValue()).isEmpty();
        assertThat(c.isHttpOnly()).isTrue();
        assertThat(c.getPath()).isEqualTo("/");
    }
}
