/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import services.AuthenticationService;

/**
 *
 * @author Victor Okonkwo
 */
public class BodyParser {

    public static Map<String, Object> bodyParserMiddleware(HttpServletRequest req) {
        Map<String, Object> requestObject;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Destructure JSON Object from request body
            // 1) Get the request body
            String requestBody = req.getReader().lines().collect(Collectors.joining());
            // 1a)Read the request body
            requestObject = objectMapper.readValue(requestBody, Map.class);
            return requestObject;

        } catch (IOException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
