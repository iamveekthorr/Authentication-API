/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor Okonkwo
 */
public class AppError extends Exception {
    HttpServletResponse statusCode;
    Object status;
    Boolean isOperational;
    public AppError(String message, HttpServletResponse statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.status = String.valueOf(statusCode.getStatus()).startsWith("4") ? "Fail" : "Success";
        this.isOperational = true;
        
        super.getStackTrace();
        
    }
    
}
