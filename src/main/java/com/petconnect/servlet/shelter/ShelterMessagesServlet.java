package com.petconnect.servlet.shelter;
import com.petconnect.servlet.MessagesServletBase;
import javax.servlet.annotation.WebServlet;
@WebServlet("/shelter/messages")
/**
 * Maps the shared messaging workflow to shelter accounts and their route.
 */
public class ShelterMessagesServlet extends MessagesServletBase {
    protected boolean adopter(){return false;}
    protected String route(){return "/shelter/messages";}
}
