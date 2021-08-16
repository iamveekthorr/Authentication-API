/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.assignment;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.json.simple.JSONObject;

/**
 *
 * @author Victor Okonkwo
 */
@WebFilter(filterName = "AuthController", urlPatterns = {"/auth/sign-in"}, dispatcherTypes = {
    DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE})
public class AuthController implements Filter {

    private static final boolean debug = true;
    private static InputStream reader;
    private static Properties properties = new Properties();

    // The filter configuration object we are associated with. If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;

    public AuthController() {
        try {
            reader = new FileInputStream("C:\\Users\\Devthorr\\Documents\\NetBeansProjects\\Assignment\\src\\resources\\config.properties");
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

    void login(ServletRequest request, ServletResponse response){
        HttpServletResponse res = HttpServletResponse.class.cast(response);
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        Object token;

        token = createJWT("USER1", "Devthorr", "Jane Doe", 1000000);
        Cookie cookie = new Cookie("jwt", token.toString());
        res.addCookie(cookie);
        try{
            JSONObject obj = new JSONObject();
            obj.put("token", token);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(obj.toJSONString());
        }catch(IOException ex){
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

    /**
     *
     * @param req The servlet request we are processing
     * @param res The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (debug) {
            log("AuthController:doFilter()");
        }
        login(req, res);
        Throwable problem = null;
        try {
            chain.doFilter(req, res);
        } catch (Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
            t.printStackTrace();
        }
        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, res);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("AuthController:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("AuthController()");
        }
        StringBuffer sb = new StringBuffer("AuthController(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); // NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); // NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (IOException ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
