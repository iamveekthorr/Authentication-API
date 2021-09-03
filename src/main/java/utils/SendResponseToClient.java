/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import services.AuthenticationService;

/**
 *
 * @author Victor Okonkwo
 */
public class SendResponseToClient {
    public static JSONObject sendResponseObject(HttpServletResponse res, String status, String message,
            Optional<String> token, Optional<Throwable> err, Optional<Map<String, Object>> userObject) {
        try {
            JSONObject responseObject = new JSONObject();
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");

            responseObject.put("status", status);
            responseObject.put("message", message);

            if (token.isPresent())responseObject.put("token", token.get());
            if (userObject.isPresent())responseObject.put("data", userObject.get());
            if (err.isPresent())responseObject.put("error", err.get());

            res.getWriter().write(responseObject.toJSONString());
            return responseObject;
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
