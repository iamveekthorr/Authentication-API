/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import io.jsonwebtoken.MalformedJwtException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author Victor Okonkwo
 */
@WebServlet(name = "ErrorController", urlPatterns = {"/*"})
public class ErrorController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("null")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject responseOobj = new JSONObject();
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

        response.setContentType("applicatin/json");
        response.setCharacterEncoding("UTF-8");
        try {
            if (((String) request.getAttribute("javax.servlet.error.servlet_name")) == null) {
                System.out.println("Entered 404");
                responseOobj.put("status", "fail");
                responseOobj.put("message", "Could not find " + request.getRequestURI() + " on this server");
                response.getWriter().write(responseOobj.toJSONString());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Logger.getAnonymousLogger().log(Level.SEVERE, "Fail", throwable);
                return;
            }

            if (throwable != null) {
                if (throwable instanceof ServletException) {
                    responseOobj.put("status", "Error");
                    responseOobj.put("message", "Something went very wrong");
                    response.getWriter().write(responseOobj.toJSONString());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    Logger.getAnonymousLogger().log(Level.SEVERE, "Fail", throwable);
                    return;
                }
                if (throwable instanceof SQLException) {
                    responseOobj.put("status", "Error");
                    responseOobj.put("message", "Something went very wrong");
                    response.getWriter().write(responseOobj.toJSONString());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    Logger.getAnonymousLogger().log(Level.SEVERE, "Fail", throwable);
                    return;
                }
                if (throwable instanceof IOException) {
                    responseOobj.put("status", "Error");
                    responseOobj.put("message", "Something went very wrong");
                    response.getWriter().write(responseOobj.toJSONString());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    Logger.getAnonymousLogger().log(Level.SEVERE, "Fail", throwable);
                    return;
                }
                if (throwable instanceof NullPointerException) {
                    responseOobj.put("status", "Error");
                    responseOobj.put("message", "Something went very wrong");
                    response.getWriter().write(responseOobj.toJSONString());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    Logger.getAnonymousLogger().log(Level.SEVERE, "Fail", throwable);
                    return;
                }

                if (throwable instanceof MalformedJwtException) {
                    responseOobj.put("status", "fail");
                    responseOobj.put("message", "Please Login and try again.");
                    response.getWriter().write(responseOobj.toJSONString());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }

        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Fail", ex);
        }
    }

    // FOR HANDLING INTERNAL SERVER ERRORS
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
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
            throws ServletException, IOException {
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
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
