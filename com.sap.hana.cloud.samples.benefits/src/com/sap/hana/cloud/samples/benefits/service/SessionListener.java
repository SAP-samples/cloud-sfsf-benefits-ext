package com.sap.hana.cloud.samples.benefits.service;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("nls")
public class SessionListener implements HttpSessionListener {

    public static final String INITIAL_FLAG = "initial";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        synchronized (this) {
            logger.info("New user session is created.");
            sessionEvent.getSession().setAttribute(INITIAL_FLAG, "true");
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
    }

}
