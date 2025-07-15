package com.example.service1.SECURITY_TESTs;

import com.example.service1.Security.JWT.UnauthorizedHandlerJwt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnauthorizedHandlerJwtTest {

    private UnauthorizedHandlerJwt handler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        handler = new UnauthorizedHandlerJwt();
        request = new MockHttpServletRequest();
        request.setServletPath("/test/path");
        response = new MockHttpServletResponse();
        authException = new BadCredentialsException("Invalid credentials");
    }

    @Test
    void commence_sets401AndJsonBody() throws IOException, ServletException {
        handler.commence(request, response, authException);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus(), "Status should be 401");
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType(), "Content type should be JSON");

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};
        Map<String, Object> body = mapper.readValue(response.getContentAsByteArray(), typeRef);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, body.get("status"), "Body status should be 401");
        assertEquals("Unauthorized", body.get("error"), "Error field should be 'Unauthorized'");
        assertEquals("Invalid credentials", body.get("message"), "Message should match exception message");
        assertEquals("/test/path", body.get("path"), "Path should match request servlet path");
    }
}
