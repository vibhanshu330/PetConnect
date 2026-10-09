package com.petconnect.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Runs before every request under /admin/*, /shelter/*, /adopter/*.
 * Blocks the request unless the session shows a logged-in user whose
 * role matches the folder being accessed.
 *
 * NOTE: this checks ROLE only, not OBJECT OWNERSHIP. Ownership checks
 * (e.g. "does this pet belong to this shelter?") happen inside the
 * individual shelter servlets themselves - see EditPetServlet etc.
 */
@WebFilter(urlPatterns = {"/admin/*", "/shelter/*", "/adopter/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                          FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        String role = (String) session.getAttribute("userRole");
        boolean allowed =
                (path.startsWith("/admin/")   && "ADMIN".equals(role)) ||
                (path.startsWith("/shelter/") && "SHELTER".equals(role)) ||
                (path.startsWith("/adopter/") && "ADOPTER".equals(role));

        if (!allowed) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=access_denied");
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }
}
