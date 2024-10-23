package com.example.learningjwt.filter;


import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyFilter1 implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("MyFilter1 doFilter");
        filterChain.doFilter(servletRequest, servletResponse);    // 계속 필터하라는 의미
    }
}
