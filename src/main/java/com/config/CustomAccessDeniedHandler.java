package com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.response.StatusResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;



@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(new StatusResponse("Access denied"));
            response.getWriter().write(json);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
