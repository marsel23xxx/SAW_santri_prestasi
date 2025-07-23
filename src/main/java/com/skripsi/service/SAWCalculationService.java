// src/main/java/com/skripsi/service/SAWCalculationService.java - FIXED VERSION
package com.skripsi.service;

import com.skripsi.dao.PenilaianDAO;
import com.skripsi.model.Penilaian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SAWCalculationService {
    private PenilaianDAO penilaianDAO;
    
    // Bobot kriteria SAW
    private static final double BOBOT_RAPORT = 0.4;
    private static final double BOBOT_AKHLAK = 0.3;
    private static final double BOBOT_EKSTRAKURIKULER = 0.2;
    private static final double BOBOT_ABSENSI = 0.1;
    
    public SAWCalculationService() {
        this.penilaianDAO = new PenilaianDAO();
    }
    
    public void calculateSAWScores() {
        List<Penilaian> allPenilaian = penilaianDAO.getAllPenilaian();
        
        if (allPenilaian.isEmpty()) {
            return;
        }
        
        // FIXED: Create copies of Penilaian objects to avoid modifying original data
        List<Penilaian> workingCopies = createWorkingCopies(allPenilaian);
        
        // Step 1: Normalisasi nilai (menggunakan copies)
        normalizeValues(workingCopies);
        
        // Step 2: Hitung skor SAW (menggunakan nilai yang sudah dinormalisasi)
        calculateScores(workingCopies);
        
        // Step 3: Ranking berdasarkan skor SAW
        assignRankings(workingCopies);
        
        // Step 4: Update database dengan skor dan ranking (menggunakan santri_id dari original)
        updateDatabase(workingCopies);
    }
    
    // ADDED: Create working copies to preserve original data
    private List<Penilaian> createWorkingCopies(List<Penilaian> originalList) {
        List<Penilaian> copies = new ArrayList<>();
        for (Penilaian original : originalList) {
            Penilaian copy = new Penilaian();
            copy.setId(original.getId());
            copy.setSantriId(original.getSantriId());
            copy.setNamaSantri(original.getNamaSantri());
            copy.setKelasSantri(original.getKelasSantri());
            copy.setNilaiRaport(original.getNilaiRaport());
            copy.setNilaiAkhlak(original.getNilaiAkhlak());
            copy.setNilaiEkstrakurikuler(original.getNilaiEkstrakurikuler());
            copy.setNilaiAbsensi(original.getNilaiAbsensi());
            copy.setSkorSAW(original.getSkorSAW());
            copy.setRanking(original.getRanking());
            copy.setCreatedBy(original.getCreatedBy());
            copies.add(copy);
        }
        return copies;
    }
    
    private void normalizeValues(List<Penilaian> penilaianList) {
        // Cari nilai maksimum untuk setiap kriteria dari data asli
        double maxRaport = penilaianList.stream()
                .mapToDouble(Penilaian::getNilaiRaport)
                .max().orElse(1.0);
        
        double maxAkhlak = penilaianList.stream()
                .mapToDouble(Penilaian::getNilaiAkhlak)
                .max().orElse(1.0);
        
        double maxEkstrakurikuler = penilaianList.stream()
                .mapToDouble(Penilaian::getNilaiEkstrakurikuler)
                .max().orElse(1.0);
        
        double maxAbsensi = penilaianList.stream()
                .mapToDouble(Penilaian::getNilaiAbsensi)
                .max().orElse(1.0);
        
        // Normalisasi menggunakan metode MAX (benefit criteria)
        // FIXED: Now modifying only working copies
        for (Penilaian p : penilaianList) {
            p.setNilaiRaport(p.getNilaiRaport() / maxRaport);
            p.setNilaiAkhlak(p.getNilaiAkhlak() / maxAkhlak);
            p.setNilaiEkstrakurikuler(p.getNilaiEkstrakurikuler() / maxEkstrakurikuler);
            p.setNilaiAbsensi(p.getNilaiAbsensi() / maxAbsensi);
        }
    }
    
    private void calculateScores(List<Penilaian> penilaianList) {
        for (Penilaian p : penilaianList) {
            double score = (BOBOT_RAPORT * p.getNilaiRaport()) +
                          (BOBOT_AKHLAK * p.getNilaiAkhlak()) +
                          (BOBOT_EKSTRAKURIKULER * p.getNilaiEkstrakurikuler()) +
                          (BOBOT_ABSENSI * p.getNilaiAbsensi());
            
            p.setSkorSAW(score);
        }
    }
    
    private void assignRankings(List<Penilaian> penilaianList) {
        // Sort berdasarkan skor SAW descending
        Collections.sort(penilaianList, 
                Comparator.comparingDouble(Penilaian::getSkorSAW).reversed());
        
        // Assign ranking
        for (int i = 0; i < penilaianList.size(); i++) {
            penilaianList.get(i).setRanking(i + 1);
        }
    }
    
    private void updateDatabase(List<Penilaian> penilaianList) {
        for (Penilaian p : penilaianList) {
            penilaianDAO.updateSkorSAW(p.getSantriId(), p.getSkorSAW(), p.getRanking());
        }
    }
    
    public List<Penilaian> getRankingResults() {
        return penilaianDAO.getAllPenilaian();
    }
    
    public List<Penilaian> getRankingByKelas(String kelas) {
        List<Penilaian> allByKelas = penilaianDAO.getPenilaianByKelas(kelas);
        
        if (!allByKelas.isEmpty()) {
            // Recalculate ranking for this class only
            Collections.sort(allByKelas, 
                    Comparator.comparingDouble(Penilaian::getSkorSAW).reversed());
            
            for (int i = 0; i < allByKelas.size(); i++) {
                allByKelas.get(i).setRanking(i + 1);
            }
        }
        
        return allByKelas;
    }
    
    public List<Penilaian> getTopSantri(int limit) {
        return penilaianDAO.getTopSantri(limit);
    }
    
    public SAWAnalysis getAnalysis() {
        List<Penilaian> allPenilaian = penilaianDAO.getAllPenilaian();
        
        if (allPenilaian.isEmpty()) {
            return new SAWAnalysis(0, 0, 0, 0, 0);
        }
        
        int totalSantri = allPenilaian.size();
        
        double avgRaport = allPenilaian.stream()
                .mapToDouble(Penilaian::getNilaiRaport)
                .average().orElse(0);
        
        double avgAkhlak = allPenilaian.stream()
                .mapToDouble(Penilaian::getNilaiAkhlak)
                .average().orElse(0);
        
        double avgEkstrakurikuler = allPenilaian.stream()
                .mapToDouble(Penilaian::getNilaiEkstrakurikuler)
                .average().orElse(0);
        
        double avgAbsensi = allPenilaian.stream()
                .mapToDouble(Penilaian::getNilaiAbsensi)
                .average().orElse(0);
        
        return new SAWAnalysis(totalSantri, avgRaport, avgAkhlak, avgEkstrakurikuler, avgAbsensi);
    }
    
    // Inner class untuk analisis
    public static class SAWAnalysis {
        private final int totalSantri;
        private final double avgRaport;
        private final double avgAkhlak;
        private final double avgEkstrakurikuler;
        private final double avgAbsensi;
        
        public SAWAnalysis(int totalSantri, double avgRaport, double avgAkhlak, 
                          double avgEkstrakurikuler, double avgAbsensi) {
            this.totalSantri = totalSantri;
            this.avgRaport = avgRaport;
            this.avgAkhlak = avgAkhlak;
            this.avgEkstrakurikuler = avgEkstrakurikuler;
            this.avgAbsensi = avgAbsensi;
        }
        
        public int getTotalSantri() { return totalSantri; }
        public double getAvgRaport() { return avgRaport; }
        public double getAvgAkhlak() { return avgAkhlak; }
        public double getAvgEkstrakurikuler() { return avgEkstrakurikuler; }
        public double getAvgAbsensi() { return avgAbsensi; }
    }
}