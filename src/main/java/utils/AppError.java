/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Victor Okonkwo
 */
public class AppError extends Exception {
    int statusCode;
    Object status;
    Boolean isOperational;
    public AppError(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        int stat = Integer.parseInt(Integer.toString(statusCode).substring(0, 1));
        this.status = String.valueOf(stat).startsWith("4") ? "Fail" : "Success";
        this.isOperational = true;
        
        super.getStackTrace();
        
    }
    
}
