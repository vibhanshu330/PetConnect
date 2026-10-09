package com.petconnect.servlet;

import com.petconnect.dao.MessageDAO;
import com.petconnect.model.MessageView;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Shared role-specific controller logic; AuthFilter enforces the role folder. */
public abstract class MessagesServletBase extends HttpServlet {
    private static final Logger LOG=Logger.getLogger(MessagesServletBase.class.getName());
    protected final MessageDAO dao=new MessageDAO();
    protected abstract boolean adopter();
    protected abstract String route();

    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        int uid=(Integer)req.getSession(false).getAttribute("userId");
        int petId=parse(req.getParameter("petId"));
        int peerId=parse(req.getParameter("peerId"));
        try {
            List<MessageView> all=dao.findMessages(uid);
            Map<String,MessageView> threads=new LinkedHashMap<>();
            for(MessageView m:all) {
                int peer=m.getSenderId()==uid?m.getReceiverId():m.getSenderId();
                if(m.getPetId()>0) threads.putIfAbsent(peer+":"+m.getPetId(),m);
            }
            req.setAttribute("threads",threads.values());
            List<MessageView> conversation=Collections.emptyList();
            if(adopter() && petId>0) {
                if(dao.canStartAdopterConversation(uid,petId)) peerId=dao.shelterForPet(petId); else petId=0;
            } else if(!adopter() && petId>0 && peerId>0) {
                if(dao.shelterForPet(petId)!=uid || !dao.isAdopter(peerId) ||
                        !(dao.hasConversation(uid,peerId,petId) || dao.hasApplication(peerId,petId))) petId=0;
            }
            if(petId>0 && peerId>0) {
                conversation=dao.findConversation(uid,peerId,petId);
                req.setAttribute("peerId",peerId); req.setAttribute("petId",petId);
                req.setAttribute("peerName",conversation.isEmpty()?"Shelter":conversation.get(0).getSenderId()==peerId?conversation.get(0).getSenderName():"Shelter");
            }
            req.setAttribute("conversation",conversation);
            req.setAttribute("adopter",adopter());
        } catch(SQLException e) {
            LOG.log(Level.SEVERE,"Unable to load messages",e); req.setAttribute("error","Messages could not be loaded right now.");
        }
        req.getRequestDispatcher(adopter()?"/adopter/messages.jsp":"/shelter/messages.jsp").forward(req,resp);
    }

    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");
        int uid=(Integer)req.getSession(false).getAttribute("userId");
        int petId=parse(req.getParameter("petId"));
        int peerId=parse(req.getParameter("peerId"));
        String body=req.getParameter("body"); body=body==null?"":body.trim();
        if(petId<1 || body.isEmpty() || body.length()>5000) { resp.sendError(400,"Enter a message of 1 to 5000 characters."); return; }
        try {
            int receiver;
            if(adopter()) {
                if(!dao.canStartAdopterConversation(uid,petId)) { resp.sendError(403); return; }
                receiver=dao.shelterForPet(petId); // receiver is always resolved from the pet owner
            } else {
                if(peerId<1 || dao.shelterForPet(petId)!=uid || !dao.isAdopter(peerId) ||
                        !(dao.hasConversation(uid,peerId,petId) || dao.hasApplication(peerId,petId))) { resp.sendError(403); return; }
                receiver=peerId;
            }
            dao.send(uid,receiver,petId,body);
            resp.sendRedirect(req.getContextPath()+route()+"?petId="+petId+(adopter()?"":"&peerId="+receiver));
        } catch(SQLException e) { LOG.log(Level.SEVERE,"Unable to save message",e); resp.sendError(500,"Message could not be sent."); }
    }
    private int parse(String value) { try{return Integer.parseInt(value);}catch(Exception ignored){return -1;} }
}
