// src/main/java/com/skripsi/controller/LoginController.java
package com.skripsi.controller;

import com.skripsi.service.AuthService;
import com.skripsi.view.DashboardView;
import javafx.stage.Stage;

public class LoginController {
    private AuthService authService;
    
    public LoginController() {
        this.authService = new AuthService();
    }
    
    public boolean handleLogin(String username, String password) {
        return authService.login(username, password);
    }
    
    public void openDashboard(Stage loginStage) {
        try {
            DashboardView dashboard = new DashboardView(authService);
            Stage dashboardStage = new Stage();
            dashboard.start(dashboardStage);
            
            // Close login window
            loginStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public AuthService getAuthService() {
        return authService;
    }
}