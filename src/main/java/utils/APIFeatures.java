/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Victor Okonkwo
 */
public class APIFeatures {
    String query; 
    HttpServletRequest queryObject;
    
    APIFeatures(String query, HttpServletRequest queryObject) {
        this.query = query;
        this.queryObject = queryObject;
    }
    
    void filter(){
        
    }
    
    void sort(){
        
    }
}
