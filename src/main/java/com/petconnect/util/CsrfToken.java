package com.petconnect.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Session-bound synchronizer token used by the CSRF filter and JSP forms. */
public final class CsrfToken {
    public static final String SESSION_ATTRIBUTE = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfToken() { }

    public static String getOrCreate(HttpSession session) {
        synchronized (session) {
            Object current = session.getAttribute(SESSION_ATTRIBUTE);
            if (current instanceof String token && !token.isBlank()) return token;
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(SESSION_ATTRIBUTE, token);
            return token;
        }
    }

    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Object expected = session.getAttribute(SESSION_ATTRIBUTE);
        String submitted = request.getParameter("csrfToken");
        if (submitted == null && request.getContentType() != null &&
                request.getContentType().toLowerCase().startsWith("multipart/form-data")) {
            try {
                var part = request.getPart("csrfToken");
                if (part != null && part.getSize() <= 128) {
                    submitted = new String(part.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.US_ASCII);
                }
            } catch (Exception ignored) {
                return false;
            }
        }
        if (!(expected instanceof String expectedToken) || submitted == null) return false;
        return MessageDigest.isEqual(expectedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                submitted.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
