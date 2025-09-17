package com.nourishcare.inventoryservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = parseJwt(request);
        
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String userId = jwtUtils.getUserIdFromJwtToken(jwt);
            request.setAttribute("userId", userId);
            return true;
        }
        
        // For now, allow requests without authentication (we'll secure gradually)
        // In production, you would return false here for protected endpoints
        return true;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        
        return null;
    }
}