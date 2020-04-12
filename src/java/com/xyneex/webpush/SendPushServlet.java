/*
 * Copyright (c) 2018, Xyneex Technologies. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * You are not meant to edit or modify this source code unless you are
 * authorized to do so.
 *
 * Please contact Xyneex Technologies, #1 Orok Orok Street, Calabar, Nigeria.
 * or visit www.xyneex.com if you need additional information or have any
 * questions.
 */
package com.xyneex.webpush;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.jose4j.lang.JoseException;
import org.json.JSONObject;

/**
 *
 * @author Jevison7x
 */
public class SendPushServlet extends HttpServlet
{

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            HttpSession session = request.getSession(false);
            if(session != null)
                if(session.getAttribute("subscription") != null)
                {
                    String subscriptionJSONString = (String)session.getAttribute("subscription");
                    JSONObject jsono = new JSONObject(subscriptionJSONString);
                    Subscription subscription = new Subscription();
                    subscription.setEndpoint(jsono.getString("endpoint"));
                    subscription.setKey(jsono.getString("key"));
                    subscription.setAuth(jsono.getString("auth"));
                    String message = "Hello World";
                    byte[] payload = message.getBytes();

                    System.out.println("endpoint: " + subscription.getEndpoint());
                    System.out.println("key: " + subscription.getKey());
                    System.out.println("auth: " + subscription.getAuth());
                    sendPushMessage(subscription, payload);
                }
                else
                    out.print("There was no subscription in the session.");
            else
                out.print("There was no session.");
        }
        catch(Exception xcp)
        {
            xcp.printStackTrace(System.out);
            xcp.printStackTrace(out);
        }
        finally
        {
            out.close();
        }
    }

    public void sendPushMessage(Subscription sub, byte[] payload) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException
    {
        Notification notification;
        PushService pushService;
        System.out.println("Creating the notification object...");
        // Create a notification with the endpoint, userPublicKey from the subscription and a custom payload
        notification = new Notification(
                sub.getEndpoint(),
                sub.getUserPublicKey(),
                sub.getAuthAsBytes(),
                payload
        );

        // Instantiate the push service, no need to use an API key for Push API
        pushService = new PushService();

        // Send the notification
        System.out.println("Sending the push notification...");
        HttpResponse httpResponse = pushService.send(notification);
        System.out.println("Response: " + httpResponse.getEntity().getContent());
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

}
