package com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.response.StatusResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(new StatusResponse("Unauthorized"));
            response.getWriter().write(json);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
