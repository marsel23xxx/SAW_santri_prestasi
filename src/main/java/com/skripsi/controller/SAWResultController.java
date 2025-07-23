// src/main/java/com/skripsi/controller/SAWResultController.java
package com.skripsi.controller;

import com.skripsi.service.SAWCalculationService;
import com.skripsi.model.Penilaian;
import com.skripsi.dao.SantriDAO;

import java.util.List;

public class SAWResultController {
    private SAWCalculationService sawService;
    private SantriDAO santriDAO;
    
    public SAWResultController() {
        this.sawService = new SAWCalculationService();
        this.santriDAO = new SantriDAO();
    }
    
    public List<Penilaian> getAllRankings() {
        return sawService.getRankingResults();
    }
    
    public List<Penilaian> getRankingsByKelas(String kelas) {
        return sawService.getRankingByKelas(kelas);
    }
    
    public List<Penilaian> getTopSantri(int limit) {
        return sawService.getTopSantri(limit);
    }
    
    public SAWCalculationService.SAWAnalysis getAnalysis() {
        return sawService.getAnalysis();
    }
    
    public List<String> getAvailableKelas() {
        return santriDAO.getDistinctKelas();
    }
    
    public void recalculateSAW() {
        sawService.calculateSAWScores();
    }
}