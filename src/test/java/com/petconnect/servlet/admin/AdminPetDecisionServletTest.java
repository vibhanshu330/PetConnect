package com.petconnect.servlet.admin;

import com.petconnect.dao.PetDAO;
import com.petconnect.model.Pet;
import com.petconnect.util.DBConnection;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminPetDecisionServletTest {

    @Test
    void approveRedirectsToSuccessOnlyWhenPendingPetWasUpdated() throws Exception {
        assertRedirect(new ApprovePetServlet(), true, "/admin/pending-pets?approved=true");
        assertRedirect(new ApprovePetServlet(), false, "/admin/pending-pets?error=true");
    }

    @Test
    void rejectRedirectsToSuccessOnlyWhenPendingPetWasUpdated() throws Exception {
        assertRedirect(new RejectPetServlet(), true, "/admin/pending-pets?rejected=true");
        assertRedirect(new RejectPetServlet(), false, "/admin/pending-pets?error=true");
    }

    @Test
    void malformedPetIdRedirectsToErrorWithoutCallingDao() throws Exception {
        ApprovePetServlet servlet = new ApprovePetServlet();
        PetDAO dao = mock(PetDAO.class);
        setDao(servlet, dao);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("petId")).thenReturn("not-an-id");
        when(request.getContextPath()).thenReturn("/petconnect");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/petconnect/admin/pending-pets?error=true");
        verify(dao, org.mockito.Mockito.never()).updateStatus(anyInt(), org.mockito.ArgumentMatchers.any());
    }

    private static void assertRedirect(Object servlet, boolean updateResult, String expected)
            throws Exception {
        PetDAO dao = mock(PetDAO.class);
        setDao(servlet, dao);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("petId")).thenReturn("42");
        when(request.getContextPath()).thenReturn("/petconnect");
        when(dao.updateStatus(eq(42), org.mockito.ArgumentMatchers.any(Pet.Status.class)))
                .thenReturn(updateResult);

        if (servlet instanceof ApprovePetServlet approve) {
            approve.doPost(request, response);
        } else {
            ((RejectPetServlet) servlet).doPost(request, response);
        }

        verify(response).sendRedirect("/petconnect" + expected);
    }

    private static void setDao(Object servlet, PetDAO dao) throws Exception {
        Field field = servlet.getClass().getDeclaredField("petDAO");
        field.setAccessible(true);
        field.set(servlet, dao);
    }
}
