package com.example.buildingmanagement.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {

    @Value("${api.key}")
    private String apiKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestApiKey = httpRequest.getHeader("X-Api-Key");

        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");

            String json = "{\"success\":false,\"message\":\"Unauthorized\",\"data\":null,"
                    + "\"errors\":[\"API Key không hợp lệ hoặc không được cung cấp. Vui lòng thêm header X-Api-Key.\"],"
                    + "\"httpStatus\":401}";

            httpResponse.getWriter().write(json);
            return;
        }

        chain.doFilter(request, response);
    }
}
