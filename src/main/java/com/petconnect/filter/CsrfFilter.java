package com.petconnect.filter;

import com.petconnect.util.CsrfToken;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Enforces synchronizer tokens for every POST, including multipart forms. */
@WebFilter(urlPatterns = "/*")
public class CsrfFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            if (!CsrfToken.isValid(httpRequest)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing security token. Reload the form and try again.");
                return;
            }
        } else if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
            CsrfToken.getOrCreate(httpRequest.getSession(true));
        }
        chain.doFilter(request, response);
    }
}
