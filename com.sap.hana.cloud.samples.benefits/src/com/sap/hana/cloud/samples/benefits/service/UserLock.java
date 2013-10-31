package com.sap.hana.cloud.samples.benefits.service;

import java.util.HashMap;
import java.util.Map;

public class UserLock {

    private static UserLock INSTANCE = new UserLock();

    public static UserLock getInstance() {
        return INSTANCE;
    }

    private Map<String, Object> userList = new HashMap<>();

    private UserLock() {
    }

    public Object getUserLock(String user) {
        synchronized (userList) {
            Object lock = userList.get(user);
            if (lock == null) {
                lock = new Object();
                userList.put(user, lock);
            }
            return lock;
        }
    }

}
