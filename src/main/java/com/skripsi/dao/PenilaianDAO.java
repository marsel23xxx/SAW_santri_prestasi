// src/main/java/com/skripsi/dao/PenilaianDAO.java
package com.skripsi.dao;

import com.skripsi.config.DatabaseConfig;
import com.skripsi.model.Penilaian;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenilaianDAO {
    
    public boolean insertPenilaian(Penilaian penilaian) {
        String sql = "INSERT INTO penilaian (santri_id, nilai_raport, nilai_akhlak, nilai_ekstrakurikuler, nilai_absensi, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, penilaian.getSantriId());
            stmt.setDouble(2, penilaian.getNilaiRaport());
            stmt.setDouble(3, penilaian.getNilaiAkhlak());
            stmt.setDouble(4, penilaian.getNilaiEkstrakurikuler());
            stmt.setDouble(5, penilaian.getNilaiAbsensi());
            stmt.setInt(6, penilaian.getCreatedBy());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Penilaian> getAllPenilaian() {
        List<Penilaian> penilaianList = new ArrayList<>();
        String sql = "SELECT p.*, s.nama as nama_santri, s.kelas as kelas_santri " +
                    "FROM penilaian p " +
                    "JOIN santri s ON p.santri_id = s.id " +
                    "ORDER BY p.skor_saw DESC, s.nama";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Penilaian penilaian = createPenilaianFromResultSet(rs);
                penilaianList.add(penilaian);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return penilaianList;
    }
    
    public Penilaian getPenilaianBySantriId(int santriId) {
        String sql = "SELECT p.*, s.nama as nama_santri, s.kelas as kelas_santri " +
                    "FROM penilaian p " +
                    "JOIN santri s ON p.santri_id = s.id " +
                    "WHERE p.santri_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, santriId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPenilaianFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updatePenilaian(Penilaian penilaian) {
        String sql = "UPDATE penilaian SET nilai_raport = ?, nilai_akhlak = ?, nilai_ekstrakurikuler = ?, nilai_absensi = ? " +
                    "WHERE santri_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, penilaian.getNilaiRaport());
            stmt.setDouble(2, penilaian.getNilaiAkhlak());
            stmt.setDouble(3, penilaian.getNilaiEkstrakurikuler());
            stmt.setDouble(4, penilaian.getNilaiAbsensi());
            stmt.setInt(5, penilaian.getSantriId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateSkorSAW(int santriId, double skorSAW, int ranking) {
        String sql = "UPDATE penilaian SET skor_saw = ?, ranking = ? WHERE santri_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, skorSAW);
            stmt.setInt(2, ranking);
            stmt.setInt(3, santriId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePenilaian(int santriId) {
        String sql = "DELETE FROM penilaian WHERE santri_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, santriId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Penilaian> getPenilaianByKelas(String kelas) {
        List<Penilaian> penilaianList = new ArrayList<>();
        String sql = "SELECT p.*, s.nama as nama_santri, s.kelas as kelas_santri " +
                    "FROM penilaian p " +
                    "JOIN santri s ON p.santri_id = s.id " +
                    "WHERE s.kelas = ? " +
                    "ORDER BY p.skor_saw DESC, s.nama";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kelas);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Penilaian penilaian = createPenilaianFromResultSet(rs);
                    penilaianList.add(penilaian);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return penilaianList;
    }
    
    public List<Penilaian> getTopSantri(int limit) {
        List<Penilaian> penilaianList = new ArrayList<>();
        String sql = "SELECT p.*, s.nama as nama_santri, s.kelas as kelas_santri " +
                    "FROM penilaian p " +
                    "JOIN santri s ON p.santri_id = s.id " +
                    "ORDER BY p.skor_saw DESC " +
                    "LIMIT ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Penilaian penilaian = createPenilaianFromResultSet(rs);
                    penilaianList.add(penilaian);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return penilaianList;
    }
    
    private Penilaian createPenilaianFromResultSet(ResultSet rs) throws SQLException {
        Penilaian penilaian = new Penilaian();
        penilaian.setId(rs.getInt("id"));
        penilaian.setSantriId(rs.getInt("santri_id"));
        penilaian.setNamaSantri(rs.getString("nama_santri"));
        penilaian.setKelasSantri(rs.getString("kelas_santri"));
        penilaian.setNilaiRaport(rs.getDouble("nilai_raport"));
        penilaian.setNilaiAkhlak(rs.getDouble("nilai_akhlak"));
        penilaian.setNilaiEkstrakurikuler(rs.getDouble("nilai_ekstrakurikuler"));
        penilaian.setNilaiAbsensi(rs.getDouble("nilai_absensi"));
        penilaian.setSkorSAW(rs.getDouble("skor_saw"));
        penilaian.setRanking(rs.getInt("ranking"));
        penilaian.setCreatedBy(rs.getInt("created_by"));
        return penilaian;
    }
}