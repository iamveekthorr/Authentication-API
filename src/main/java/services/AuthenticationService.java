/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import models.UserModel;
import org.json.simple.JSONObject;
import utils.AppError;
import utils.PropLoader;

/**
 *
 * @author Victor Okonkwo
 */
public class AuthenticationService {

    public AuthenticationService() {

    }

    // Sign user token based on currently logged in user 
    public static JwtBuilder signToken(String id) {
        return Jwts.builder().setId(id);
    }

    // create and send Token to the client 
    public static String createJWT(String id, String issuer, String subject, long ttlMillis) {
        Map<String, Object> cookieOptions = new HashMap<>();
        cookieOptions.put("httpOnly", true);
        cookieOptions.put("secure", true);

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(PropLoader.loadPropertiesFile()
                .getProperty("SECERET_KEY"));
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Let's set the JWT Claims
        JwtBuilder builder = signToken(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer).signWith(signingKey,
                signatureAlgorithm).setClaims(cookieOptions);

        // if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    // Decode JWT and verify the Token 
    public static Claims decodeJWT(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected
        Claims claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(DatatypeConverter.parseBase64Binary(
                    PropLoader.loadPropertiesFile().getProperty("SECERET_KEY")))
                    .build().parseClaimsJws(jwt).getBody();
            return claims;
        } catch (MalformedJwtException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
        return null;
    }

    public void signup(HttpServletRequest req, HttpServletResponse res) {
        Object token;
        String email, password, firstName, lastName, confirmPassword;
        UserService currentUser;

        try {

            ObjectMapper objectMapper = new ObjectMapper();

            // Destructure JSON Object from request body 
            // 1) Get the request body 
            String reqBody = req.getReader().lines().collect(Collectors.joining());
            // 1a)Read the request body
            Map<String, Object> userObject = objectMapper.readValue(reqBody, Map.class);

            email = (String) userObject.get("email");
            password = (String) userObject.get("password");
            confirmPassword = (String) userObject.get("passwordConfirm");
            firstName = (String) userObject.get("firstName");
            lastName = (String) userObject.get("lastName");

            // 1b) Check fields for null values in request bidy
            if (email == null || password == null || confirmPassword == null
                    || firstName == null || lastName == null
                    || email.isBlank() || password.isBlank() || confirmPassword.isBlank()
                    || firstName.isBlank() || lastName.isBlank()) {
                // Throw error if the values are not found 
                try {
                    res.setStatus(HttpServletResponse.SC_CONFLICT);
                    JSONObject responseOobj = new JSONObject();
                    responseOobj.put("status", "fail");
                    responseOobj.put("message", "All fields are required");
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write(responseOobj.toJSONString());
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
                return;
            }
            // 1c) Check length of password
            if (password.length() < 8) {
                throw new AppError("Password must be equal to more than 8 characters", res);
            }

            // 1d) Compare passwords
            if (!password.equalsIgnoreCase(confirmPassword)) {
                try {
                    res.setStatus(HttpServletResponse.SC_CONFLICT);
                    JSONObject responseOobj = new JSONObject();
                    responseOobj.put("status", "fail");
                    responseOobj.put("message", "Password Mismatch");
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write(responseOobj.toJSONString());
                    return;
                } catch (IOException ex) {
                    Logger.getAnonymousLogger(AuthenticationService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

            // 2) Check if user exists in the database  
            currentUser = new UserService();

            // 2a) Get model from Service
            UserModel userModel = currentUser.getByEmail(email, Optional.empty());
            if (userModel != null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                JSONObject responseOobj = new JSONObject();
                responseOobj.put("status", "fail");
                responseOobj.put("message", "User already Exists");
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(responseOobj.toJSONString());
                return;
            }

            // 2c) Add fields to model using setters
            userModel = new UserModel();
            userModel.setEmail(email);
            userModel.setFirstName(firstName);
            userModel.setLastName(lastName);
            userModel.setPassword(password);
            currentUser.save(userModel);

            // 2d) Create JSONWebToken
            token = createJWT(String.valueOf(userModel.getID()),
                    PropLoader.loadPropertiesFile().getProperty("JWT_ISSUER"),
                    firstName.concat(" ").concat(lastName), 1000000);
            // 3) Create new Cookie  
            Cookie cookie = new Cookie("jwt", token.toString());
            // 3a) Add token to cookie
            res.addCookie(cookie);
            // 3b) Create JSON response object  
            JSONObject responseOobj = new JSONObject();
            
            // 3c) create new user object to send to client 
            Map<String, Object> newUserObject =  new HashMap<>();            
            newUserObject.put("_id", userModel.getID());
            newUserObject.put("firstName", userModel.getFirstName());
            newUserObject.put("lastName", userModel.getLastName());
            newUserObject.put("email", userModel.getEmail());
            
            // 4a) Create response object and send to client
            responseOobj.put("status", "success");
            responseOobj.put("message", "User created successfully");
            responseOobj.put("token", token);
            responseOobj.put("data", newUserObject);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            
            // 4b) Write object to client 
            res.getWriter().write(responseOobj.toJSONString());
        } catch (AppError | IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
    }

    public void login(HttpServletRequest req, HttpServletResponse res) {
        Object token;
        String email, password;
        UserService currentUser;
        JSONObject responseOobj;

        try {

            ObjectMapper objectMapper = new ObjectMapper();

            // Destructure JSON Object from request body 
            // 1) Get the request body 
            String reqBody = req.getReader().lines().collect(Collectors.joining());
            // 1a)Read the request body
            Map<String, Object> userObject = objectMapper.readValue(reqBody, Map.class);

            email = (String) userObject.get("email");
            password = (String) userObject.get("password");

            // 1b) Check fields for null values in request bidy
            if (email == null || password == null || email.isBlank() || password.isBlank()) {
                // Throw error if the values are not found 
                try {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseOobj = new JSONObject();
                    responseOobj.put("status", "fail");
                    responseOobj.put("message", "Email and Password are both required");
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write(responseOobj.toJSONString());
                    throw new AppError("Email and Password are both required", res);
                } catch (AppError ex) {
                    Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
                return;
            }
            // 2) Check if user exists and password is correct 
            currentUser = new UserService();

            // 2a) Get model from Service
            UserModel userModel = currentUser.getByEmail(email, Optional.of(password));
            if (userModel == null) {
                try {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseOobj = new JSONObject();
                    responseOobj.put("status", "fail");
                    responseOobj.put("message", "Email and Password are both required");
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write(responseOobj.toJSONString());
                    throw new AppError("Incorrect login Credentials. Please check email or password", res);
                } catch (AppError ex) {
                    Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
                return;
            }

            // 2b) Create JSONWebToken
            token = createJWT(String.valueOf(userModel.getID()),
                    PropLoader.loadPropertiesFile().getProperty("JWT_ISSUER"),
                    userModel.getFirstName(), 1000000);

            // 3) Create new Cookie  
            Cookie cookie = new Cookie("jwt", token.toString());
            // 3a) Add token to cookie
            res.addCookie(cookie);
            
            // 3a) Create user object
            Map<String, Object> newUserObject =  new HashMap<>();            
            newUserObject.put("_id", userModel.getID());
            newUserObject.put("firstName", userModel.getFirstName());
            newUserObject.put("lastName", userModel.getLastName());
            newUserObject.put("email", userModel.getEmail());
            
            // 3b) Create JSON response object  
            responseOobj = new JSONObject();
            responseOobj.put("token", token);
            responseOobj.put("status", "success");
            responseOobj.put("message", "user successfuly logged in");
            responseOobj.put("data", newUserObject);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(responseOobj.toJSONString());

        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
    }

    public void logOut(HttpServletRequest req, HttpServletResponse res) {
        Object token = "LoggedOut";
        Cookie cookie = new Cookie("jwt", token.toString());
        cookie.setMaxAge(0);
        res.addCookie(cookie);
        try {
            JSONObject obj = new JSONObject();
            obj.put("token", "Logged out");
            obj.put("status", "success");
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(obj.toJSONString());
        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
    }

    void protect(HttpServletRequest req, HttpServletResponse res) {
        Object token = null;
        try {
            if (req.getHeader("Authorization") != null
                    && req.getHeader("Authorization").startsWith("Bearer")) {
                token = req.getHeader("Authorization").split(" ", 1);
            } else if (req.getCookies() != null) {
                token = req.getHeader("Cookie");
            }

            if (token == null) {
                try {
                    throw new AppError("Please login and try again", res);
                } catch (AppError ex) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage(), ex.getCause());
                }
                return;
            }

            try {
                Object currentUser = decodeJWT(token.toString()).getId();
                if (currentUser == null) {
                    try {
                        throw new AppError("This user does not exist", res);
                    } catch (AppError ex) {
                        Logger.getAnonymousLogger().log(Level.SEVERE, "Fail", ex);
                    }
                }
            } catch (MalformedJwtException ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Fail", ex);
            }

        } catch (NullPointerException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
    }

}
