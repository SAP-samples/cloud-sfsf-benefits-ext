package com.sap.benefits.management.api;

import java.security.Principal;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class BaseService {
    
    protected static final Response RESPONSE_OK = Response.ok("ok").build(); //$NON-NLS-1$
    protected static final Response RESPONSE_BAD = Response.status(Status.BAD_REQUEST).build();
    
    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;

    @Resource
    protected ServletContext context;
    
    public String getLoggedInUserId() {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null.");
        }
        String userId = null;

        if (request instanceof HttpServletRequest) {
            Principal userPrincipal = ((HttpServletRequest) request).getUserPrincipal();
            if (userPrincipal != null) {
                userId = userPrincipal.getName();
            }
        }

        return userId;
    }

}
