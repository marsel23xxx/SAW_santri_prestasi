// src/main/java/com/skripsi/service/AuthService.java
package com.skripsi.service;

import com.skripsi.dao.UserDAO;
import com.skripsi.model.User;

public class AuthService {
    private UserDAO userDAO;
    private User currentUser;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    public boolean login(String username, String password) {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        return false;
    }
    
    public void logout() {
        this.currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
    
    public boolean isGuru() {
        return currentUser != null && "guru".equals(currentUser.getRole());
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}