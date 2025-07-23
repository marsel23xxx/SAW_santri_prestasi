// src/main/java/com/skripsi/controller/InputDataController.java
package com.skripsi.controller;

import com.skripsi.dao.SantriDAO;
import com.skripsi.dao.PenilaianDAO;
import com.skripsi.model.Santri;
import com.skripsi.model.Penilaian;
import com.skripsi.service.AuthService;
import com.skripsi.service.SAWCalculationService;

import java.util.List;

public class InputDataController {
    private SantriDAO santriDAO;
    private PenilaianDAO penilaianDAO;
    private AuthService authService;
    private SAWCalculationService sawService;
    
    public InputDataController(AuthService authService) {
        this.authService = authService;
        this.santriDAO = new SantriDAO();
        this.penilaianDAO = new PenilaianDAO();
        this.sawService = new SAWCalculationService();
    }
    
    // Santri Management
    public boolean addSantri(String nama, String kelas, String tahunAjaran) {
        if (nama == null || nama.trim().isEmpty() ||
            kelas == null || kelas.trim().isEmpty() ||
            tahunAjaran == null || tahunAjaran.trim().isEmpty()) {
            return false;
        }
        
        Santri santri = new Santri(nama.trim(), kelas.trim(), tahunAjaran.trim());
        return santriDAO.insertSantri(santri);
    }
    
    public boolean updateSantri(int id, String nama, String kelas, String tahunAjaran) {
        if (nama == null || nama.trim().isEmpty() ||
            kelas == null || kelas.trim().isEmpty() ||
            tahunAjaran == null || tahunAjaran.trim().isEmpty()) {
            return false;
        }
        
        Santri santri = new Santri(nama.trim(), kelas.trim(), tahunAjaran.trim());
        santri.setId(id);
        return santriDAO.updateSantri(santri);
    }
    
    public boolean deleteSantri(int santriId) {
        // Delete penilaian first due to foreign key constraint
        penilaianDAO.deletePenilaian(santriId);
        return santriDAO.deleteSantri(santriId);
    }
    
    public List<Santri> getAllSantri() {
        return santriDAO.getAllSantri();
    }
    
    public List<Santri> getSantriByKelas(String kelas) {
        return santriDAO.getSantriByKelas(kelas);
    }
    
    public List<String> getDistinctKelas() {
        return santriDAO.getDistinctKelas();
    }
    
    // Penilaian Management
    public boolean addPenilaian(int santriId, double nilaiRaport, double nilaiAkhlak, 
                               double nilaiEkstrakurikuler, double nilaiAbsensi) {
        
        if (!isValidNilai(nilaiRaport) || !isValidNilai(nilaiAkhlak) ||
            !isValidNilai(nilaiEkstrakurikuler) || !isValidNilai(nilaiAbsensi)) {
            return false;
        }
        
        // Check if penilaian already exists for this santri
        Penilaian existing = penilaianDAO.getPenilaianBySantriId(santriId);
        if (existing != null) {
            // Update existing penilaian
            existing.setNilaiRaport(nilaiRaport);
            existing.setNilaiAkhlak(nilaiAkhlak);
            existing.setNilaiEkstrakurikuler(nilaiEkstrakurikuler);
            existing.setNilaiAbsensi(nilaiAbsensi);
            return penilaianDAO.updatePenilaian(existing);
        } else {
            // Create new penilaian
            Penilaian penilaian = new Penilaian(santriId, nilaiRaport, nilaiAkhlak, 
                                              nilaiEkstrakurikuler, nilaiAbsensi, 
                                              authService.getCurrentUser().getId());
            return penilaianDAO.insertPenilaian(penilaian);
        }
    }
    
    public Penilaian getPenilaianBySantriId(int santriId) {
        return penilaianDAO.getPenilaianBySantriId(santriId);
    }
    
    public List<Penilaian> getAllPenilaian() {
        return penilaianDAO.getAllPenilaian();
    }
    
    public boolean deletePenilaian(int santriId) {
        return penilaianDAO.deletePenilaian(santriId);
    }
    
    // SAW Calculation
    public void calculateSAW() {
        sawService.calculateSAWScores();
    }
    
    private boolean isValidNilai(double nilai) {
        return nilai >= 0 && nilai <= 100;
    }
    
    // Validation
    public ValidationResult validatePenilaianInput(String raport, String akhlak, 
                                                  String ekstrakurikuler, String absensi) {
        try {
            double nilaiRaport = Double.parseDouble(raport);
            double nilaiAkhlak = Double.parseDouble(akhlak);
            double nilaiEkstrakurikuler = Double.parseDouble(ekstrakurikuler);
            double nilaiAbsensi = Double.parseDouble(absensi);
            
            if (!isValidNilai(nilaiRaport)) {
                return new ValidationResult(false, "Nilai raport harus antara 0-100");
            }
            if (!isValidNilai(nilaiAkhlak)) {
                return new ValidationResult(false, "Nilai akhlak harus antara 0-100");
            }
            if (!isValidNilai(nilaiEkstrakurikuler)) {
                return new ValidationResult(false, "Nilai ekstrakurikuler harus antara 0-100");
            }
            if (!isValidNilai(nilaiAbsensi)) {
                return new ValidationResult(false, "Nilai absensi harus antara 0-100");
            }
            
            return new ValidationResult(true, "");
            
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Semua nilai harus berupa angka");
        }
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}