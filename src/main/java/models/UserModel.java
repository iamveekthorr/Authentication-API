/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;


/**
 *
 * @author Victor Okonkwo
 */

public class UserModel {
    String userName;
    String password;
    String email;
    private int id;

    public void setID(int id) {
        this.id = id;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public int getID() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
