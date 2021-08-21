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
import java.io.InputStream;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import models.UserModel;
import org.json.simple.JSONObject;
import utils.AppError;

/**
 *
 * @author Victor Okonkwo
 */
public class AuthenticationService {

    private static InputStream reader;
    public static Properties properties = new Properties();

    public AuthenticationService() {
        try {
            reader = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            properties.load(reader);
        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
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
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(
                properties.getProperty("SECERET_KEY"));
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
                    properties.getProperty("SECERET_KEY")))
                    .build().parseClaimsJws(jwt).getBody();
            return claims;
        } catch (MalformedJwtException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
        return null;
    }

    public void login(HttpServletRequest req, HttpServletResponse res) {
        Object token;
        List<UserModel> user = null;
        String email, password;

        try {

            ObjectMapper objectMapper = new ObjectMapper();

            // Destructure JSON Object from request body 
            // 1) Get the request body 
            String reqBody = req.getReader().lines().collect(Collectors.joining());
            Map<String, Object> userObject = objectMapper.readValue(reqBody, Map.class);

            email = (String) userObject.get("email");
            password = (String) userObject.get("password");

            // Check fields for null values
            if (email == null || password == null) {
                // Throw error if the values are not found 
                try {
                    throw new AppError("Email and Password are both required", 400);
                } catch (AppError ex) {
                    Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
                return;
            }
            // 2( Check if user exists and password is correct 
            Object currentUser = new UserService().getByEmail(email);
            if (currentUser == null) {
                try {
                    System.out.print("No user found");
                    throw new AppError("Email and Password are both required", 400);
                } catch (AppError ex) {
                    Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
                return;
            }
            System.out.println("Email " + email + "\nPassword " + password);

            // Create JSONWebToken
            token = createJWT("USER1", "Devthorr", "Jane Doe", 1000000);
            // Create new Cookie  
            Cookie cookie = new Cookie("jwt", token.toString());
            // Add token to cookie
            res.addCookie(cookie);
            JSONObject obj = new JSONObject();
            obj.put("token", token);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(obj.toJSONString());
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
                    throw new AppError("Please login and try again", 401);
                } catch (AppError ex) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage(), ex.getCause());
                }
                return;
            }
            // Create better error handling 
            try {
                Object currentUser = decodeJWT(token.toString()).getId();
                System.out.println("Current User: ".concat((String) currentUser));
                if (currentUser == null) {
                    try {
                        throw new AppError("This user does not exist", 401);
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
