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
    String statusCode;
    String status;
    Boolean isOperational;
    public AppError(String message, String statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.status = statusCode.startsWith("4") ? "fail" : "error";
        this.isOperational = true;
        
        super.getStackTrace();
    }
    
}
