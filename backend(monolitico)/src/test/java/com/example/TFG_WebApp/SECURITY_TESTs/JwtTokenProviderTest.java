package com.example.TFG_WebApp.SECURITY_TESTs;

import com.example.TFG_WebApp.Security.JWT.JwtTokenProvider;
import com.example.TFG_WebApp.Security.JWT.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new JwtTokenProvider();
        String secret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
        ReflectionTestUtils.setField(provider, "jwtSecret", secret);
        ReflectionTestUtils.setField(JwtTokenProvider.class, "JWT_EXPIRATION_IN_MS", 1000L);
        ReflectionTestUtils.setField(JwtTokenProvider.class, "REFRESH_TOKEN_EXPIRATION_MSEC", 2000L);
        ReflectionTestUtils.setField(provider, "userDetailsService", userDetailsService);
    }

    @Test
    void generateToken_and_validate_success() {
        User user = new User("user1", "pwd", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Token tokenObj = provider.generateToken(user);
        String token = tokenObj.getTokenValue();
        assertNotNull(token);
        assertEquals(Token.TokenType.ACCESS, tokenObj.getTokenType());
        assertTrue(provider.validateToken(token));
        assertEquals("user1", provider.getUsername(token));
    }

    @Test
    void generateRefreshToken_and_validate_success() {
        User user = new User("user2", "pwd", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Token tokenObj = provider.generateRefreshToken(user);
        String token = tokenObj.getTokenValue();
        assertNotNull(token);
        assertEquals(Token.TokenType.REFRESH, tokenObj.getTokenType());
        assertTrue(provider.validateToken(token));
        assertEquals("user2", provider.getUsername(token));
    }

    @Test
    void validateToken_malformed_returnsFalse() {
        assertFalse(provider.validateToken("xxx.yyy.zzz"));
    }

    @Test
    void validateToken_expired_returnsFalse() throws InterruptedException {
        User user = new User("user3", "pwd", List.of());
        Token tokenObj = provider.generateToken(user);
        Thread.sleep(1100L);
        assertFalse(provider.validateToken(tokenObj.getTokenValue()));
    }

    @Test
    void resolveToken_withBearerHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer abcd");
        assertEquals("abcd", provider.resolveToken(req));
    }

    @Test
    void resolveToken_withoutBearerHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        assertNull(provider.resolveToken(req));
        req.addHeader("Authorization", "Token xyz");
        assertNull(provider.resolveToken(req));
    }

    /*@Test
    void getAuthentication_returnsAuthentication() {
        String username = "userAuth";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        User userDetails = new User(username, "pwd", List.of(authority));
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        Token tokenObj = provider.generateToken(userDetails);
        Authentication auth = provider.getAuthentication(tokenObj.getTokenValue());
        assertNotNull(auth);
        assertEquals(username, auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }*/
}
