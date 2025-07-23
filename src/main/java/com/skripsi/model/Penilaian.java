// src/main/java/com/skripsi/model/Penilaian.java
package com.skripsi.model;

public class Penilaian {
    private int id;
    private int santriId;
    private String namaSantri;
    private String kelasSantri;
    private double nilaiRaport;
    private double nilaiAkhlak;
    private double nilaiEkstrakurikuler;
    private double nilaiAbsensi;
    private double skorSAW;
    private int ranking;
    private int createdBy;
    
    // Constructors
    public Penilaian() {}
    
    public Penilaian(int santriId, double nilaiRaport, double nilaiAkhlak, 
                    double nilaiEkstrakurikuler, double nilaiAbsensi, int createdBy) {
        this.santriId = santriId;
        this.nilaiRaport = nilaiRaport;
        this.nilaiAkhlak = nilaiAkhlak;
        this.nilaiEkstrakurikuler = nilaiEkstrakurikuler;
        this.nilaiAbsensi = nilaiAbsensi;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getSantriId() { return santriId; }
    public void setSantriId(int santriId) { this.santriId = santriId; }
    
    public String getNamaSantri() { return namaSantri; }
    public void setNamaSantri(String namaSantri) { this.namaSantri = namaSantri; }
    
    public String getKelasSantri() { return kelasSantri; }
    public void setKelasSantri(String kelasSantri) { this.kelasSantri = kelasSantri; }
    
    public double getNilaiRaport() { return nilaiRaport; }
    public void setNilaiRaport(double nilaiRaport) { this.nilaiRaport = nilaiRaport; }
    
    public double getNilaiAkhlak() { return nilaiAkhlak; }
    public void setNilaiAkhlak(double nilaiAkhlak) { this.nilaiAkhlak = nilaiAkhlak; }
    
    public double getNilaiEkstrakurikuler() { return nilaiEkstrakurikuler; }
    public void setNilaiEkstrakurikuler(double nilaiEkstrakurikuler) { this.nilaiEkstrakurikuler = nilaiEkstrakurikuler; }
    
    public double getNilaiAbsensi() { return nilaiAbsensi; }
    public void setNilaiAbsensi(double nilaiAbsensi) { this.nilaiAbsensi = nilaiAbsensi; }
    
    public double getSkorSAW() { return skorSAW; }
    public void setSkorSAW(double skorSAW) { this.skorSAW = skorSAW; }
    
    public int getRanking() { return ranking; }
    public void setRanking(int ranking) { this.ranking = ranking; }
    
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}