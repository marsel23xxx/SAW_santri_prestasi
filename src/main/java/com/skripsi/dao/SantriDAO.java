// src/main/java/com/skripsi/dao/SantriDAO.java
package com.skripsi.dao;

import com.skripsi.config.DatabaseConfig;
import com.skripsi.model.Santri;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SantriDAO {
    
    public boolean insertSantri(Santri santri) {
        String sql = "INSERT INTO santri (nama, kelas, tahun_ajaran) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, santri.getNama());
            stmt.setString(2, santri.getKelas());
            stmt.setString(3, santri.getTahunAjaran());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Santri> getAllSantri() {
        List<Santri> santriList = new ArrayList<>();
        String sql = "SELECT * FROM santri ORDER BY nama";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Santri santri = new Santri();
                santri.setId(rs.getInt("id"));
                santri.setNama(rs.getString("nama"));
                santri.setKelas(rs.getString("kelas"));
                santri.setTahunAjaran(rs.getString("tahun_ajaran"));
                santriList.add(santri);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return santriList;
    }
    
    public List<Santri> getSantriByKelas(String kelas) {
        List<Santri> santriList = new ArrayList<>();
        String sql = "SELECT * FROM santri WHERE kelas = ? ORDER BY nama";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kelas);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Santri santri = new Santri();
                    santri.setId(rs.getInt("id"));
                    santri.setNama(rs.getString("nama"));
                    santri.setKelas(rs.getString("kelas"));
                    santri.setTahunAjaran(rs.getString("tahun_ajaran"));
                    santriList.add(santri);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return santriList;
    }
    
    public Santri getSantriById(int id) {
        String sql = "SELECT * FROM santri WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Santri santri = new Santri();
                    santri.setId(rs.getInt("id"));
                    santri.setNama(rs.getString("nama"));
                    santri.setKelas(rs.getString("kelas"));
                    santri.setTahunAjaran(rs.getString("tahun_ajaran"));
                    return santri;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateSantri(Santri santri) {
        String sql = "UPDATE santri SET nama = ?, kelas = ?, tahun_ajaran = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, santri.getNama());
            stmt.setString(2, santri.getKelas());
            stmt.setString(3, santri.getTahunAjaran());
            stmt.setInt(4, santri.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteSantri(int santriId) {
        String sql = "DELETE FROM santri WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, santriId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<String> getDistinctKelas() {
        List<String> kelasList = new ArrayList<>();
        String sql = "SELECT DISTINCT kelas FROM santri ORDER BY kelas";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                kelasList.add(rs.getString("kelas"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kelasList;
    }
}