package com.sap.benefits.management.api;

import java.security.Principal;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.model.User;

public class BaseService {
    
    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;

    @Resource
    protected ServletContext context;
    
    protected UserDAO userDAO = new UserDAO();
    
    protected String getLoggedInUserId() {
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
    
    protected User getLoggedInUser(){
    	return userDAO.getByUserId(getLoggedInUserId());
    }
    
    protected Response createBadRequestResponse(String reponseText){
    	return Response.status(Status.BAD_REQUEST).entity(reponseText).build();
    }
    
    protected Response createOkResponse(){
    	return Response.ok().build();
    }

}
