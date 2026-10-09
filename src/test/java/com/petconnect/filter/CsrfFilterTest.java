package com.petconnect.filter;

import com.petconnect.util.CsrfToken;
import org.junit.jupiter.api.Test;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsrfFilterTest {
    private final CsrfFilter filter = new CsrfFilter();

    @Test void acceptsPostWithSessionBoundTokenAcrossImportantRouteTypes() throws Exception {
        for (String path : new String[]{"/profile", "/adopter/apply", "/shelter/add-pet", "/admin/delete-user", "/logout"}) {
            HttpServletRequest req = mock(HttpServletRequest.class);
            HttpServletResponse res = mock(HttpServletResponse.class);
            HttpSession session = mock(HttpSession.class);
            FilterChain chain = mock(FilterChain.class);
            when(req.getMethod()).thenReturn("POST");
            when(req.getRequestURI()).thenReturn(path);
            when(req.getSession(false)).thenReturn(session);
            when(session.getAttribute(CsrfToken.SESSION_ATTRIBUTE)).thenReturn("secret-token");
            when(req.getParameter("csrfToken")).thenReturn("secret-token");
            filter.doFilter(req, res, chain);
            verify(chain).doFilter(req, res);
            verify(res, never()).sendError(anyInt(), anyString());
        }
    }

    @Test void rejectsMissingAndInvalidTokens() throws Exception {
        for (String submitted : new String[]{null, "wrong"}) {
            HttpServletRequest req = mock(HttpServletRequest.class);
            HttpServletResponse res = mock(HttpServletResponse.class);
            HttpSession session = mock(HttpSession.class);
            FilterChain chain = mock(FilterChain.class);
            when(req.getMethod()).thenReturn("POST");
            when(req.getSession(false)).thenReturn(session);
            when(session.getAttribute(CsrfToken.SESSION_ATTRIBUTE)).thenReturn("secret-token");
            when(req.getParameter("csrfToken")).thenReturn(submitted);
            filter.doFilter(req, res, chain);
            verify(res).sendError(eq(403), anyString());
            verify(chain, never()).doFilter(any(), any());
        }
    }

    @Test void acceptsSessionTokenFromMultipartUploadPart() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        Part part = mock(Part.class);
        FilterChain chain = mock(FilterChain.class);
        when(req.getMethod()).thenReturn("POST");
        when(req.getContentType()).thenReturn("multipart/form-data; boundary=example");
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfToken.SESSION_ATTRIBUTE)).thenReturn("upload-token");
        when(req.getParameter("csrfToken")).thenReturn(null);
        when(req.getPart("csrfToken")).thenReturn(part);
        when(part.getSize()).thenReturn(12L);
        when(part.getInputStream()).thenReturn(new ByteArrayInputStream("upload-token".getBytes()));
        filter.doFilter(req, res, chain);
        verify(chain).doFilter(req, res);
        verify(res, never()).sendError(anyInt(), anyString());
    }
}
