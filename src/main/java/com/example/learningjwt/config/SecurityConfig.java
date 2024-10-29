package com.example.learningjwt.config;

import com.example.learningjwt.config.jwt.JwtAuthenticationFilter;
import com.example.learningjwt.config.jwt.JwtAuthorizationFilter;
import com.example.learningjwt.filter.MyFilter1;
import com.example.learningjwt.repository.UserRepository;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(corsFilter)      // 인증이 없을때는 @CrossOrigin을 컨트롤러에 등록, 인증이 있을때는 필터에 등록
                .httpBasic(AbstractHttpConfigurer::disable) // header Authorization에 ID, PW를 담아 보내는 기본 방식을 안쓰겠다는 의미
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilter(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration)))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration), userRepository))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN"))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();

    }
}
