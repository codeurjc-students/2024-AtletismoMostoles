package com.example.service1.SECURITY_TESTs;

import com.example.service1.Security.JWT.JwtCookieManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.assertj.core.api.Assertions.assertThat;

class JwtCookieManagerTest {

    private static JwtCookieManager cookieManager;

    @BeforeAll
    static void setup() {
        cookieManager = new JwtCookieManager();
    }

    @Test
    void whenCreateAccessTokenCookie_thenCorrectProperties() {
        String rawToken = "myAccessToken";
        ResponseCookie cookie = (ResponseCookie) cookieManager.createAccessTokenCookie(rawToken);

        // Name and encrypted value
        assertThat(cookie.getName()).isEqualTo(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME);
        assertThat(cookie.getValue()).isNotEmpty().isNotEqualTo(rawToken);

        // Cookie attributes
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(-1);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    void whenCreateRefreshTokenCookie_thenCorrectProperties() {
        String rawToken = "myRefreshToken";
        ResponseCookie cookie = (ResponseCookie) cookieManager.createRefreshTokenCookie(rawToken);

        assertThat(cookie.getName()).isEqualTo(JwtCookieManager.REFRESH_TOKEN_COOKIE_NAME);
        assertThat(cookie.getValue()).isNotEmpty().isNotEqualTo(rawToken);

        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(-1);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    void whenDeleteAccessTokenCookie_thenMaxAgeZeroAndEmptyValue() {
        ResponseCookie cookie = (ResponseCookie) cookieManager.deleteAccessTokenCookie();

        assertThat(cookie.getName()).isEqualTo(JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME);
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(0);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }
}
