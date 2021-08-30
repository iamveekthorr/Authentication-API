/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor Okonkwo
 */
@WebFilter(filterName = "SetRequestSecurityHeaders", urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE})
public class SetRequestHeaders implements Filter {

    private static final boolean DEBUG = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    public static final String POLICY = "script-src 'self'";

    public SetRequestHeaders() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (DEBUG) {
            log("SetRequestSecurityHeaders:DoBeforeProcessing");
        }
        System.getenv().forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
        // Write code here to process the request and/or response before
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log items on the request object,
        // such as the parameters.
        if (response instanceof HttpServletResponse) {
            ((HttpServletResponse) response).setHeader("Content-Security-Policy", POLICY);
            ((HttpServletResponse) response).setHeader("X-Frame-Options", "SAMEORIGIN");
            ((HttpServletResponse) response).setHeader("Strict-Transport-Security", "max-age=15552000; includeSubDomains");
            ((HttpServletResponse) response).setHeader("X-Content-Type-Options", "nosniff");
            ((HttpServletResponse) response).setHeader("X-Permitted-Cross-Domain-Policies", "none");
            ((HttpServletResponse) response).setHeader("Referrer-Policy", "no-referrer");
            ((HttpServletResponse) response).setHeader("Expect-CT", "enforce, max-age=0");
            ((HttpServletResponse) response).setHeader("X-Download-Options", "noopen");
        }
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (DEBUG) {
            log("SetRequestSecurityHeaders:doFilter()");
        }

        doBeforeProcessing(request, response);
        chain.doFilter(request, response);
    }

    /**
     * Return the filter configuration object for this filter.
     *
     * @return
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
    @Override
    public void destroy() {
    }

    /**
     * Init method for this filter
     *
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (DEBUG) {
                log("SetRequestSecurityHeaders:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("SetRequestSecurityHeaders()");
        }
        StringBuilder sb = new StringBuilder("SetRequestSecurityHeaders(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
