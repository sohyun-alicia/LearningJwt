package com.example.learningjwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.learningjwt.config.auth.PrincipalDetails;
import com.example.learningjwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에 UsernamePasswordAuthenticationFilter가 있음
// /login 요청해서 username, password 요청하면(POST)
// UsernamePasswordAuthenticationFilter 동작
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해 실행되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: 로그인 시도중");

        // 1. username, password 받아서
        try {
            // 원시적인 방법
//            BufferedReader br = req.getReader();
//            String input = null;
//            while((input = br.readLine()) != null) {
//                System.out.println("input = " + input);
//            }

            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(req.getInputStream(), User.class);
            System.out.println("user = " + user);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // authenticationManager로 로그인 시도를 하면 PrincipalDetailsService 호출 -> loadUserByUsername
            // 실행후 정상(DB의 username, password와 일치)이면 authentication 리턴됨
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("principalDetails.getUser() = " + principalDetails.getUser());
            // return authentication => authentication 객체가 session 영역에 저장됨 => 로그인 되었다는 뜻
            // 리턴의 잉는 권한 관리를 Security가 대신 해주기 때문에 편리하기 위함
            // 굳이 JWT토큰을 사용하면서 세션을 만들 이유 없음. 단지 권한 처리 때문에 session에 넣어줌

            return authentication;

        } catch (IOException e) {
//            throw new RuntimeException(e);
        }

        return null;
    }


    // attemptAuthenticaion 실행 후 인증이 정상적으로 인증되었으면 successfulAuthentication 메서드 실행됨
    // 여기서 JWT 토큰을 만ㄷ르어 request한 사용자에게 토큰을 response해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("JwtAuthenticationFilter.successfulAuthentication");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        // RSA 방식 아닌 Hash암호방식
        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))   // 10분
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUsername())
                .sign(Algorithm.HMAC256("cos"));
        response.addHeader("Authorization", "Bearer " + jwtToken);
//        super.successfulAuthentication(request, response, chain, authResult);
    }
}
