/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.HashMap;
import java.util.Map;
import models.UserModel;

/**
 *
 * @author Victor Okonkwo
 */
public class CreateUserJSONObject {
     public static Map<String, Object> userObject(UserModel model) {
        Map<String, Object> newUserObject = new HashMap<>();
        newUserObject.put("_id", model.getID());
        newUserObject.put("firstName", model.getFirstName());
        newUserObject.put("lastName", model.getLastName());
        newUserObject.put("email", model.getEmail());
        return newUserObject;
    }
}
