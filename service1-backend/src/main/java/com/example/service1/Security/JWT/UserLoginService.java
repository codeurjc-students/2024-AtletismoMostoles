package com.example.service1.Security.JWT;

import com.example.service1.DTO.EventNotificationDto;
import com.example.service1.Entities.User;
import com.example.service1.Services.UserService;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificacionData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class UserLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtCookieManager cookieUtil;

    @Autowired
    private UserService userService;

    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest, String encryptedAccessToken, String encryptedRefreshToken) {

        // Capturar el lastLogin antes de cualquier autenticación
        User dbUser = userService.getUserByName(loginRequest.getUsername());
        LocalDateTime lastLoginPrevio = dbUser.getLastLogin();

        System.out.println("TimeStamp Login (previo): " + lastLoginPrevio);

        // Autenticación
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = SecurityCipher.decrypt(encryptedAccessToken);
        String refreshToken = SecurityCipher.decrypt(encryptedRefreshToken);

        String username = loginRequest.getUsername();
        UserDetails user = userDetailsService.loadUserByUsername(username);

        // Ya tienes dbUser cargado arriba
        List<EventNotificationDto> notificaciones = List.of();

        if (dbUser != null) {
            List<NotificacionData> rawNotificaciones = userService.getPendingNotifications(dbUser, lastLoginPrevio);
            notificaciones = rawNotificaciones.stream().map(data -> {
                EventNotificationDto dto = new EventNotificationDto();
                dto.setEventoId(data.getEventoId());
                dto.setName(data.getName());
                dto.setDate(LocalDate.parse(data.getDate()));
                dto.setMapLink(data.getMapLink());
                dto.setImageLink(data.getImageLink());
                dto.setOrganizedByClub(data.getOrganizedByClub());
                dto.setTimestampNotificacion(data.getTimestampNotificacion());
                dto.setDisciplineIds(new HashSet<>(data.getDisciplineIdsList()));
                return dto;
            }).toList();

            // Actualizar último login después de recoger las notificaciones
            userService.updateLastLogin(dbUser);
        }

        // Resto igual...
        boolean accessTokenValid = jwtTokenProvider.validateToken(accessToken);
        boolean refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);

        HttpHeaders responseHeaders = new HttpHeaders();
        Token newAccessToken;
        Token newRefreshToken;

        if (!accessTokenValid && !refreshTokenValid) {
            newAccessToken = jwtTokenProvider.generateToken(user);
            newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        } else if (!accessTokenValid) {
            newAccessToken = jwtTokenProvider.generateToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
        } else {
            newAccessToken = jwtTokenProvider.generateToken(user);
            newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
                "Auth successful. Tokens are created in cookie.");
        loginResponse.setNotificacionesPendientes(notificaciones);

        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }

    public ResponseEntity<AuthResponse> refresh(String encryptedRefreshToken) {
        String refreshToken = SecurityCipher.decrypt(encryptedRefreshToken);

        boolean refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);

        if (!refreshTokenValid) {
            AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.FAILURE,
                    "Invalid refresh token !");
            return ResponseEntity.ok().body(loginResponse);
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(username);

        Token newAccessToken = jwtTokenProvider.generateToken(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil
                .createAccessTokenCookie(newAccessToken.getTokenValue()).toString());

        AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
                "Auth successful. Tokens are created in cookie.");
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }

    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session;
        SecurityContextHolder.clearContext();
        session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookie.setMaxAge(0);
                cookie.setValue("");
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        return "logout successfully";
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createAccessTokenCookie(token.getTokenValue()).toString());
    }

    private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createRefreshTokenCookie(token.getTokenValue()).toString());
    }
}
