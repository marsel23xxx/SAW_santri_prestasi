// src/main/java/com/skripsi/model/Santri.java
package com.skripsi.model;

public class Santri {
    private int id;
    private String nama;
    private String kelas;
    private String tahunAjaran;
    
    // Constructors
    public Santri() {}
    
    public Santri(String nama, String kelas, String tahunAjaran) {
        this.nama = nama;
        this.kelas = kelas;
        this.tahunAjaran = tahunAjaran;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    
    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }
    
    public String getTahunAjaran() { return tahunAjaran; }
    public void setTahunAjaran(String tahunAjaran) { this.tahunAjaran = tahunAjaran; }
    
    @Override
    public String toString() {
        return nama + " - " + kelas;
    }
}