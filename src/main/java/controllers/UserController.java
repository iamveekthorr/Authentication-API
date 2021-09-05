/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import services.UserService;
import utils.CreateUserJSONObject;

/**
 *
 * @author Victor Okonkwo
 */
public class UserController {

    public JSONObject findAll(HttpServletResponse res) {
        try {
            JSONObject responseObject = new JSONObject();
            
            UserService userController = new UserService();
            JSONArray userArr = new JSONArray();
            userController.findAll().forEach(user -> {
                userArr.add(CreateUserJSONObject.userObject(user));
                CreateUserJSONObject.userObject(user);
                
            });
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            responseObject.put("status", "success");
            responseObject.put("data", userArr);
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write(responseObject.toJSONString());
            return responseObject;
        } catch (IOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
