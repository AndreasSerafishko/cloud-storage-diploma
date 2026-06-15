package com.cloudstorage.security;

import com.cloudstorage.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtProvider jwtProvider;
    
    @Autowired
    private TokenRepository tokenRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        if (path.equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authToken = request.getHeader("auth-token");
        
        if (authToken != null && jwtProvider.validateToken(authToken)) {
            tokenRepository.findByTokenAndActiveTrue(authToken).ifPresent(token -> {
                String login = jwtProvider.getLoginFromToken(authToken);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(login, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }
        
        filterChain.doFilter(request, response);
    }
}
