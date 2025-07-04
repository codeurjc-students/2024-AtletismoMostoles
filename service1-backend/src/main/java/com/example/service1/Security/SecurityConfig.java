package com.example.service1.Security;

import com.example.service1.Security.JWT.JwtRequestFilter;
import com.example.service1.Security.JWT.UnauthorizedHandlerJwt;
import com.example.service1.Services.RepositoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    public RepositoryUserDetailsService userDetailService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    private static final String ADMIN = "ADMIN";
    private static final String ADMIN_URL = "/api/admin/**";
    private static final String ATHLETES = "/api/athletes**";
    private static final String COACHES = "/api/coaches**";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PUBLIC ENDPOINTS
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/results/pdf/*").permitAll()
                        // PRIVATE ENDPOINTS
                        .requestMatchers(HttpMethod.POST, ADMIN_URL ).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, ADMIN_URL).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, ADMIN_URL).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, ADMIN_URL).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, ATHLETES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, ATHLETES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, ATHLETES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, COACHES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, COACHES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, COACHES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE).authenticated()
                        .requestMatchers(HttpMethod.POST).authenticated()
                        .requestMatchers(HttpMethod.PUT).authenticated()
                        // PUBLIC ENDPOINTS
                        .anyRequest().permitAll()
                );

        // Disable Form login Authentication
        http.formLogin(AbstractHttpConfigurer::disable);

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(AbstractHttpConfigurer::disable);

        // Disable Basic Authentication
        http.httpBasic(AbstractHttpConfigurer::disable);

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
