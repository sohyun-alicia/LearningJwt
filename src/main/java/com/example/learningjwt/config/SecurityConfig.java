package com.example.learningjwt.config;

import com.example.learningjwt.filter.MyFilter1;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CorsFilter corsFilter;
    private final MyFilter1 myFilter1;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
//        http                .addFilterBefore (myFilter1, BasicAuthenticationFilter.class);

        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(corsFilter)      // 인증이 없을때는 @CrossOrigin을 컨트롤러에 등록, 인증이 있을때는 필터에 등록
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN"))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // .httpBasic().disable() -> header Authorization에 ID, PW를 담아 보내는 기본 방식을 안쓰겠다는 의미
                // .formLogin().disable()
                .build();

    }
}
