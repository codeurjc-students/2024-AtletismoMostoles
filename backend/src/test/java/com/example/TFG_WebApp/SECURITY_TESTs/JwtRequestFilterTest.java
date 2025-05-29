package com.example.TFG_WebApp.SECURITY_TESTs;

import com.example.TFG_WebApp.Security.JWT.JwtCookieManager;
import com.example.TFG_WebApp.Security.JWT.JwtRequestFilter;
import com.example.TFG_WebApp.Security.JWT.JwtTokenProvider;
import com.example.TFG_WebApp.Security.JWT.SecurityCipher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    private JwtRequestFilter filter;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new JwtRequestFilter();
        ReflectionTestUtils.setField(filter, "userDetailsService", userDetailsService);
        ReflectionTestUtils.setField(filter, "jwtTokenProvider", jwtTokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_withValidCookie_setsAuthentication() throws ServletException, IOException {
        String rawToken = "encryptedJwt";
        String decryptedToken = "rawJwt";
        String username = "testUser";

        Cookie cookie = new Cookie(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME, rawToken);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();

        try (MockedStatic<SecurityCipher> cipherMock = Mockito.mockStatic(SecurityCipher.class)) {
            cipherMock.when(() -> SecurityCipher.decrypt(rawToken)).thenReturn(decryptedToken);

            when(jwtTokenProvider.validateToken(decryptedToken)).thenReturn(true);
            when(jwtTokenProvider.getUsername(decryptedToken)).thenReturn(username);

            UserDetails userDetails = new User(username, "pwd", List.of(new SimpleGrantedAuthority("ROLE_USER")));
            when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth, "Authentication should be set in SecurityContext");
            assertEquals(username, auth.getName(), "Username should match");
            assertTrue(auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER")), "Authority ROLE_USER should be present");
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilter_withoutCookie_noAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "No authentication should be set");
        verify(filterChain).doFilter(request, response);
    }
}
