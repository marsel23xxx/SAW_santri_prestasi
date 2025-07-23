// src/main/java/com/skripsi/controller/DashboardController.java
package com.skripsi.controller;

import com.skripsi.dao.SantriDAO;
import com.skripsi.dao.PenilaianDAO;
import com.skripsi.service.AuthService;
import com.skripsi.service.SAWCalculationService;
import com.skripsi.model.Santri;
import com.skripsi.model.Penilaian;

import java.util.List;

public class DashboardController {
    private AuthService authService;
    private SantriDAO santriDAO;
    private PenilaianDAO penilaianDAO;
    private SAWCalculationService sawService;
    
    public DashboardController(AuthService authService) {
        this.authService = authService;
        this.santriDAO = new SantriDAO();
        this.penilaianDAO = new PenilaianDAO();
        this.sawService = new SAWCalculationService();
    }
    
    public DashboardStats getDashboardStats() {
        List<Santri> allSantri = santriDAO.getAllSantri();
        List<Penilaian> allPenilaian = penilaianDAO.getAllPenilaian();
        List<Penilaian> topSantri = sawService.getTopSantri(5);
        List<String> kelasList = santriDAO.getDistinctKelas();
        
        return new DashboardStats(
            allSantri.size(),
            allPenilaian.size(),
            topSantri,
            kelasList.size()
        );
    }
    
    public AuthService getAuthService() {
        return authService;
    }
    
    public static class DashboardStats {
        private final int totalSantri;
        private final int totalPenilaian;
        private final List<Penilaian> topSantri;
        private final int totalKelas;
        
        public DashboardStats(int totalSantri, int totalPenilaian, 
                            List<Penilaian> topSantri, int totalKelas) {
            this.totalSantri = totalSantri;
            this.totalPenilaian = totalPenilaian;
            this.topSantri = topSantri;
            this.totalKelas = totalKelas;
        }
        
        public int getTotalSantri() { return totalSantri; }
        public int getTotalPenilaian() { return totalPenilaian; }
        public List<Penilaian> getTopSantri() { return topSantri; }
        public int getTotalKelas() { return totalKelas; }
    }
}