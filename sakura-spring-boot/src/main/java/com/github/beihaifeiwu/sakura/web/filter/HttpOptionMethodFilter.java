package com.github.beihaifeiwu.sakura.web.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liupin on 2017/3/22.
 */
public class HttpOptionMethodFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            setHeader(response, "Access-Control-Allow-Origin", "*");
            setHeader(response, "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            setHeader(response, "Allow", response.getHeader("Access-Control-Allow-Methods"));
            setHeader(response, "Pragma", "no-cache");
            setHeader(response, "Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
            setHeader(response, "X-Frame-Options", "DENY");
            response.flushBuffer();
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void setHeader(HttpServletResponse response, String name, String value) {
        if (!response.containsHeader(name)) {
            response.setHeader(name, value);
        }
    }
}
