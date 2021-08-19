/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import dao.UserDao;

/**
 *
 * @author Victor Okonkwo
 */
public class UserController {
    
    public UserController() {
        
    }
    
    
    void printStuff(){
        UserDao userDao = new UserDao();
        System.out.println("Dao " + userDao);
    }
    
    
}
