/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import models.UserModel;
import org.mindrot.jbcrypt.BCrypt;
import utils.AppError;
import utils.BodyParser;
import utils.DotEnvLoader;
import utils.SendResponseToClient;

/**
 *
 * @author Victor Okonkwo
 */
public class AuthenticationService {

    public AuthenticationService() {

    }

    // Sign user token based on currently logged in user 
    private JwtBuilder signToken(String id) {
        return Jwts.builder().setId(id);
    }

    // create and send Token to the client 
    private String createJWT(String id, String issuer, String subject, long ttlMillis) {
        Map<String, Object> cookieOptions = new HashMap<>();
        cookieOptions.put("httpOnly", true);
        cookieOptions.put("secure", true);
        
        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        
        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(DotEnvLoader.getDotenv().get("SECERET_KEY"));
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        
        // Let's set the JWT Claims
        JwtBuilder builder = signToken(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer).signWith(signingKey,
                signatureAlgorithm).addClaims(cookieOptions);
        
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
    private Claims decodeJWT(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected
        Claims claims = Jwts.parserBuilder().setSigningKey(DatatypeConverter.parseBase64Binary(
                DotEnvLoader.getDotenv().get("SECERET_KEY"))).build().parseClaimsJws(jwt).getBody();
        System.out.println("Claims: " + claims);
        return claims;
    }

    private Map<String, Object> userObject(UserModel model) {
        Map<String, Object> newUserObject = new HashMap<>();
        newUserObject.put("_id", model.getID());
        newUserObject.put("firstName", model.getFirstName());
        newUserObject.put("lastName", model.getLastName());
        newUserObject.put("email", model.getEmail());
        return newUserObject;
    }

    public void signup(HttpServletRequest req, HttpServletResponse res) {
        Object token;
        String email, password, firstName, lastName, confirmPassword;
        UserService currentUser;
        final String SALT = BCrypt.gensalt(12);

        try {
            Map<String, Object> userObject = BodyParser.bodyParserMiddleware(req);
            email = (String) userObject.get("email");
            password = (String) userObject.get("password");
            confirmPassword = (String) userObject.get("passwordConfirm");
            firstName = (String) userObject.get("firstName");
            lastName = (String) userObject.get("lastName");

            // 1b) Check fields for null values in request bidy
            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()
                    || firstName.isBlank() || lastName.isBlank()) {
                // Throw error if the values are not found 
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                SendResponseToClient.sendResponseObject(res, "fail", "All fields are requireds", Optional.empty(),
                        Optional.empty(), Optional.empty());
                return;
            }
            // 1c) Check length of password
            if (password.length() < 8) {
                throw new AppError("Password must be equal to more than 8 characters", res);
            }

            // 1d) Compare passwords
            if (!password.equalsIgnoreCase(confirmPassword)) {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                SendResponseToClient.sendResponseObject(res, "fail", "Password Mismatch", Optional.empty(),
                        Optional.empty(), Optional.empty());
                return;

            }

            // 2) Check if user exists in the database  
            currentUser = new UserService();

            // 2a) Get model from Service
            UserModel userModel = currentUser.findByEmail(email, Optional.empty());
            if (userModel != null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                SendResponseToClient.sendResponseObject(res, "fail", "User already Exists", Optional.empty(),
                        Optional.empty(), Optional.empty());
                return;
            }

            // 2c) Add fields to model using setters
            userModel = new UserModel();
            userModel.setID(UUID.randomUUID().toString());
            userModel.setEmail(email);
            userModel.setFirstName(firstName);
            userModel.setLastName(lastName);
            userModel.setPassword(BCrypt.hashpw(password, SALT));
            currentUser.save(userModel);

            // 2d) Create JSONWebToken
            token = createJWT(String.valueOf(userModel.getID()),
                    DotEnvLoader.getDotenv().get("JWT_ISSUER"),
                    firstName.concat(" ").concat(lastName), 60 * 60 * 24);
           
            // 3) Create new Cookie  
            Cookie cookie = new Cookie("jwt", token.toString());
            // 3a) Add token to cookie
            res.addCookie(cookie);
            // 4a) Create response object and send to client
            SendResponseToClient.sendResponseObject(res, "success", "User LoggedIn successfully", Optional.of(token.toString()),
                    Optional.empty(), Optional.of(userObject(userModel)));
        } catch (AppError ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
    }

    public void login(HttpServletRequest req, HttpServletResponse res) {
        Object token;
        String email, password;
        UserService currentUser;

        Map<String, Object> userObject = BodyParser.bodyParserMiddleware(req);
        email = (String) userObject.get("email");
        password = (String) userObject.get("password");
        // 1b) Check fields for null values in request bidy
        if (email.isBlank() || password.isBlank()) {
            // Throw error if the values are not found
            try {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                SendResponseToClient.sendResponseObject(res, "fail", "Email and Password are both required", Optional.empty(),
                        Optional.empty(), Optional.empty());
                throw new AppError("Email and Password are both required", res);
            } catch (AppError ex) {
                Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            return;
        }
        // 2) Check if user exists and password is correct
        currentUser = new UserService();
        // 2a) Get model from Service
        UserModel userModel = currentUser.findByEmail(email, Optional.of(password));
        if (userModel == null) {
            try {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                SendResponseToClient.sendResponseObject(res, "fail", "Incorrect login Credentials. Please check email or password",
                        Optional.empty(), Optional.empty(), Optional.empty());
                throw new AppError("Incorrect login Credentials. Please check email or password", res);
            } catch (AppError ex) {
                Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            return;
        }
        // 2b) Create JSONWebToken
        token = createJWT(String.valueOf(userModel.getID()),
                DotEnvLoader.getDotenv().get("SECRET_KEY"),
                userModel.getFirstName(), 60 * 60 * 24);
        System.out.println("UId = " + userModel.getID());
        
        // 3) Create new Cookie
        Cookie cookie = new Cookie("jwt", token.toString());
        // 3a) Add token to cookie
        res.addCookie(cookie);
        // 3b) Create JSON response object
        SendResponseToClient.sendResponseObject(res, "success", "User Created Successfully", Optional.of(token.toString()),
                Optional.empty(), Optional.of(userObject(userModel)));
    }

    public void logOut(HttpServletRequest req, HttpServletResponse res) {
        Object token = "LoggedOut";
        Cookie cookie = new Cookie("jwt", token.toString());
        cookie.setMaxAge(0);
        res.addCookie(cookie);
        SendResponseToClient.sendResponseObject(res, "success", "logged Out", Optional.empty(),
                Optional.empty(), Optional.empty());
    }

    public void protect(HttpServletRequest req, HttpServletResponse res) {
        Object token = null;
        try {
            if (req.getHeader("Authorization") != null
                    && req.getHeader("Authorization").startsWith("Bearer")) {
                token = req.getHeader("Authorization").split(" ", 1);
                System.out.println("Token: " + decodeJWT(token.toString()));
            } else if (req.getCookies() != null && req.getCookies().length > 0) {
                token = req.getCookies()[-1];
            }

            if (token == null) {
                SendResponseToClient.sendResponseObject(res, "fail", "Please login to continue", Optional.empty(),
                        Optional.empty(), Optional.empty());
                return;
            }

            try {
                // 1) Get user ID from the Generated Token 
                Object currentUserID = decodeJWT(token.toString()).getId();
                System.out.println(currentUserID + "User id is <-");

                // 2) Check if current user is an authenticated user
                UserService userService = new UserService();
                Object currentUser = userService.findById(currentUserID);

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
