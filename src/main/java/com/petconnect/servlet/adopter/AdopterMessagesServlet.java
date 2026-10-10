package com.petconnect.servlet.adopter;
import com.petconnect.servlet.MessagesServletBase;
import javax.servlet.annotation.WebServlet;
@WebServlet("/adopter/messages")
/**
 * Maps the shared messaging workflow to adopter accounts and their route.
 */
public class AdopterMessagesServlet extends MessagesServletBase {
    protected boolean adopter(){return true;}
    protected String route(){return "/adopter/messages";}
}
