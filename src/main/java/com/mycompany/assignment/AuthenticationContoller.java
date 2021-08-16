/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.assignment;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.json.simple.JSONObject;

/**
 *
 * @author Victor Okonkwo
 */
public class AuthenticationContoller {

    private static InputStream reader;
    private static Properties properties = new Properties();

    public AuthenticationContoller() {
        try {
            reader = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            properties.load(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        // This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parserBuilder().setSigningKey(DatatypeConverter.parseBase64Binary(
                properties.getProperty("SECERET_KEY")))
                .build().parseClaimsJws(jwt).getBody();
        return claims;
    }

    void login(ServletRequest request, ServletResponse response) {
        HttpServletResponse res = HttpServletResponse.class.cast(response);
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        Object token;

        token = createJWT("USER1", "Devthorr", "Jane Doe", 1000000);
        Cookie cookie = new Cookie("jwt", token.toString());
        res.addCookie(cookie);
        try {
            JSONObject obj = new JSONObject();
            obj.put("token", token);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(obj.toJSONString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void protect(ServletRequest request, ServletResponse response) {
        HttpServletResponse res = HttpServletResponse.class.cast(response);
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        Object token = null;
        try {
            if (req.getHeader("Authorization") != null
                    && req.getHeader("Authorization").startsWith("Bearer")) {
                token = req.getHeader("Authorization").split(" ", 1);
                return;
            } else if (req.getCookies() != null) {
                token = req.getHeader("Cookie");
                return;
            }

            if (token == null) {
                System.out.println("No Token");
                // CREATE ERROR CONTROLLER
                return;
            }
            // Create better error handling 
            decodeJWT(token.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
