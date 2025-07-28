// src/main/java/com/skripsi/controller/ReportController.java
package com.skripsi.controller;

import com.skripsi.dao.PenilaianDAO;
import com.skripsi.dao.SantriDAO;
import com.skripsi.model.Penilaian;
import com.skripsi.model.Santri;
import com.skripsi.service.SAWCalculationService;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReportController {
    private PenilaianDAO penilaianDAO;
    private SantriDAO santriDAO;
    private SAWCalculationService sawService;
    private DecimalFormat df = new DecimalFormat("#.####");
    
    // PDF Fonts and Colors
    private Font titleFont;
    private Font headerFont;
    private Font contentFont;
    private Font smallFont;
    private BaseColor primaryColor = new BaseColor(70, 130, 180); // Steel Blue
    private BaseColor secondaryColor = new BaseColor(46, 125, 50); // Green
    private BaseColor grayColor = new BaseColor(117, 117, 117);
    
    public ReportController() {
        this.penilaianDAO = new PenilaianDAO();
        this.santriDAO = new SantriDAO();
        this.sawService = new SAWCalculationService();
        initializeFonts();
    }
    
    private void initializeFonts() {
        try {
            titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
            headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            contentFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, grayColor);
        } catch (Exception e) {
            // Fallback to default fonts
            titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, primaryColor);
            headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
            contentFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            smallFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, grayColor);
        }
    }
    
    // Report 1: Ranking Report (Overall)
    public RankingReport generateRankingReport() {
        List<Penilaian> rankings = sawService.getRankingResults();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return new RankingReport(rankings, timestamp);
    }
    
    // Report 2: Class Comparison Report
    public ClassComparisonReport generateClassComparisonReport() {
        List<String> kelasList = santriDAO.getDistinctKelas();
        List<ClassStatistics> classStats = new ArrayList<>();
        
        for (String kelas : kelasList) {
            List<Penilaian> kelasData = sawService.getRankingByKelas(kelas);
            if (!kelasData.isEmpty()) {
                ClassStatistics stats = calculateClassStatistics(kelas, kelasData);
                classStats.add(stats);
            }
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return new ClassComparisonReport(classStats, timestamp);
    }
    
    // Report 3: Criteria Analysis Report
    public CriteriaAnalysisReport generateCriteriaAnalysisReport() {
        List<Penilaian> allPenilaian = penilaianDAO.getAllPenilaian();
        
        if (allPenilaian.isEmpty()) {
            return new CriteriaAnalysisReport(new CriteriaStatistics(0, 0, 0, 0, 0, 0, 0, 0), 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
        
        CriteriaStatistics stats = calculateCriteriaStatistics(allPenilaian);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        return new CriteriaAnalysisReport(stats, timestamp);
    }
    
    // Report 4: Performance Summary Report
    public PerformanceSummaryReport generatePerformanceSummaryReport() {
        List<Penilaian> allPenilaian = sawService.getRankingResults();
        List<Penilaian> topPerformers = sawService.getTopSantri(5);
        List<Penilaian> lowPerformers = getLowPerformers(allPenilaian, 5);
        
        PerformanceStatistics stats = calculatePerformanceStatistics(allPenilaian);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        return new PerformanceSummaryReport(topPerformers, lowPerformers, stats, timestamp);
    }
    
    // PDF Export functions
    public boolean exportRankingReportToPDF(String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            RankingReport report = generateRankingReport();
            
            // Header
            addPDFHeader(document, "LAPORAN RANKING SANTRI BERPRESTASI", report.getTimestamp());
            
            // Statistics Summary
            addRankingStatistics(document, report.getRankings());
            
            // Ranking Table
            addRankingTable(document, report.getRankings());
            
            // Footer
            addPDFFooter(document);
            
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean exportClassComparisonToPDF(String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            ClassComparisonReport report = generateClassComparisonReport();
            
            // Header
            addPDFHeader(document, "LAPORAN PERBANDINGAN ANTAR KELAS", report.getTimestamp());
            
            // Class Statistics
            addClassComparisonTable(document, report.getClassStatistics());
            
            // Summary Statistics
            addClassSummaryStatistics(document, report.getClassStatistics());
            
            // Footer
            addPDFFooter(document);
            
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean exportCriteriaAnalysisToPDF(String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            CriteriaAnalysisReport report = generateCriteriaAnalysisReport();
            CriteriaStatistics stats = report.getStatistics();
            
            // Header
            addPDFHeader(document, "LAPORAN ANALISIS KRITERIA PENILAIAN", report.getTimestamp());
            
            // Criteria Analysis Table
            addCriteriaAnalysisTable(document, stats);
            
            // Weight Distribution
            addWeightDistribution(document);
            
            // Footer
            addPDFFooter(document);
            
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean exportPerformanceSummaryToPDF(String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            PerformanceSummaryReport report = generatePerformanceSummaryReport();
            
            // Header
            addPDFHeader(document, "LAPORAN RINGKASAN PERFORMA SANTRI", report.getTimestamp());
            
            // Performance Statistics
            addPerformanceStatistics(document, report.getStatistics());
            
            // Top Performers
            addPerformersTable(document, "TOP 5 PERFORMERS", report.getTopPerformers(), secondaryColor);
            
            // Low Performers
            addPerformersTable(document, "SANTRI YANG PERLU PERHATIAN KHUSUS", report.getLowPerformers(), BaseColor.ORANGE);
            
            // Footer
            addPDFFooter(document);
            
            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // PDF Helper Methods
    private void addPDFHeader(Document document, String title, String timestamp) throws DocumentException {
        try {
            // Create main table with 2 columns: left for logo, right for text
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{30, 70}); // 30% for logo, 70% for text

            // Left cell for logo
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setVerticalAlignment(Element.ALIGN_TOP);
            leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            try {
                // Try to load logo from resources
                String logoPath = "./src/main/java/main/resources/icons/logo_pesantren.png"; // Adjust path as needed
                Image logo = Image.getInstance(logoPath);

                // Scale logo appropriately
                logo.scaleToFit(80, 80); // Adjust size as needed
                logo.setAlignment(Element.ALIGN_LEFT);

                leftCell.addElement(logo);

            } catch (Exception e) {
                // If logo not found, add placeholder text
                Paragraph logoPlaceholder = new Paragraph("LOGO", smallFont);
                logoPlaceholder.setAlignment(Element.ALIGN_LEFT);
                leftCell.addElement(logoPlaceholder);

                // Log the error (optional)
                System.out.println("Logo not found: " + e.getMessage());
            }

            // Right cell for institution info
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            // Institution information
            Paragraph institution = new Paragraph("PONDOK PESANTREN DARUSSALAM", headerFont);
            institution.setAlignment(Element.ALIGN_CENTER);
            rightCell.addElement(institution);

            Paragraph subtitle = new Paragraph("JL. Bandung - Tasikmalaya KM 60", contentFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            rightCell.addElement(subtitle);

            Paragraph subtitle1 = new Paragraph("Sindangsari, Kersamanah", contentFont);
            subtitle1.setAlignment(Element.ALIGN_CENTER);
            rightCell.addElement(subtitle1);

            // Add cells to header table
            headerTable.addCell(leftCell);
            headerTable.addCell(rightCell);

            // Add header table to document
            document.add(headerTable);

            // Add some spacing
            document.add(new Paragraph(" ", contentFont));

            // Line separator
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            // Report title
            Paragraph reportTitle = new Paragraph(title, titleFont);
            reportTitle.setAlignment(Element.ALIGN_CENTER);
            reportTitle.setSpacingAfter(10);
            document.add(reportTitle);

            // Timestamp
            Paragraph timestampPara = new Paragraph("Tanggal Laporan: " + timestamp, smallFont);
            timestampPara.setAlignment(Element.ALIGN_CENTER);
            timestampPara.setSpacingAfter(20);
            document.add(timestampPara);

        } catch (Exception e) {
            // Fallback to original header if there's any error
    //        addOriginalPDFHeader(document, title, timestamp);
        }
    }

    
    private void addRankingStatistics(Document document, List<Penilaian> rankings) throws DocumentException {
        int total = rankings.size();
        int berprestasi = (int) rankings.stream().filter(p -> p.getRanking() <= 3).count();
        int baik = (int) rankings.stream().filter(p -> p.getRanking() > 3 && p.getRanking() <= 10).count();
        int perluDitingkatkan = total - berprestasi - baik;
        
        Paragraph statsTitle = new Paragraph("STATISTIK RINGKASAN", headerFont);
        statsTitle.setSpacingBefore(10);
        statsTitle.setSpacingAfter(10);
        document.add(statsTitle);
        
        PdfPTable statsTable = new PdfPTable(4);
        statsTable.setWidthPercentage(100);
        
        // Headers
        addStatsCell(statsTable, "Total Santri", String.valueOf(total), primaryColor);
        addStatsCell(statsTable, "Berprestasi", String.valueOf(berprestasi), secondaryColor);
        addStatsCell(statsTable, "Baik", String.valueOf(baik), BaseColor.ORANGE);
        addStatsCell(statsTable, "Perlu Ditingkatkan", String.valueOf(perluDitingkatkan), BaseColor.RED);
        
        document.add(statsTable);
        document.add(Chunk.NEWLINE);
    }
    
    private void addStatsCell(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(color);
        cell.setBorderWidth(2);
        cell.setPadding(10);
        
        Paragraph labelPara = new Paragraph(label, smallFont);
        labelPara.setAlignment(Element.ALIGN_CENTER);
        
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, color);
        Paragraph valuePara = new Paragraph(value, valueFont);
        valuePara.setAlignment(Element.ALIGN_CENTER);
        
        cell.addElement(labelPara);
        cell.addElement(valuePara);
        table.addCell(cell);
    }
    
    private void addRankingTable(Document document, List<Penilaian> rankings) throws DocumentException {
        Paragraph tableTitle = new Paragraph("DETAIL RANKING SANTRI", headerFont);
        tableTitle.setSpacingBefore(10);
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{10, 25, 15, 15, 15, 20});
        
        // Headers
        addTableHeader(table, "Rank");
        addTableHeader(table, "Nama Santri");
        addTableHeader(table, "Kelas");
        addTableHeader(table, "Skor SAW");
        addTableHeader(table, "Status");
        addTableHeader(table, "Kategori");
        
        // Data rows
        for (Penilaian p : rankings) {
            addTableCell(table, String.valueOf(p.getRanking()));
            addTableCell(table, p.getNamaSantri());
            addTableCell(table, p.getKelasSantri());
            addTableCell(table, df.format(p.getSkorSAW()));
            
            String status;
            BaseColor statusColor;
            if (p.getRanking() <= 3) {
                status = "Berprestasi";
                statusColor = secondaryColor;
            } else if (p.getRanking() <= 10) {
                status = "Baik";
                statusColor = BaseColor.ORANGE;
            } else {
                status = "Perlu Ditingkatkan";
                statusColor = BaseColor.RED;
            }
            
            addColoredTableCell(table, status, statusColor);
            addTableCell(table, getPerformanceCategory(p.getSkorSAW()));
        }
        
        document.add(table);
    }
    
    private void addClassComparisonTable(Document document, List<ClassStatistics> classStats) throws DocumentException {
        Paragraph tableTitle = new Paragraph("PERBANDINGAN STATISTIK ANTAR KELAS", headerFont);
        tableTitle.setSpacingBefore(10);
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15, 18, 20, 18, 18, 20});
        
        // Headers
        addTableHeader(table, "Kelas");
        addTableHeader(table, "Jml Santri");
        addTableHeader(table, "Rata-rata");
        addTableHeader(table, "Tertinggi");
        addTableHeader(table, "Terendah");
        addTableHeader(table, "Berprestasi");
        
        // Data rows
        for (ClassStatistics stats : classStats) {
            addTableCell(table, stats.getKelas());
            addTableCell(table, String.valueOf(stats.getJumlahSantri()));
            addTableCell(table, df.format(stats.getAvgScore()));
            addTableCell(table, df.format(stats.getMaxScore()));
            addTableCell(table, df.format(stats.getMinScore()));
            addTableCell(table, String.valueOf(stats.getJumlahBerprestasi()));
        }
        
        document.add(table);
    }
    
    private void addCriteriaAnalysisTable(Document document, CriteriaStatistics stats) throws DocumentException {
        Paragraph tableTitle = new Paragraph("ANALISIS KRITERIA PENILAIAN", headerFont);
        tableTitle.setSpacingBefore(10);
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{30, 25, 25, 20});
        
        // Headers
        addTableHeader(table, "Kriteria");
        addTableHeader(table, "Rata-rata");
        addTableHeader(table, "Nilai Tertinggi");
        addTableHeader(table, "Bobot SAW");
        
        // Data rows
        addTableCell(table, "Nilai Raport");
        addTableCell(table, String.format("%.2f", stats.getAvgRaport()));
        addTableCell(table, String.format("%.2f", stats.getMaxRaport()));
        addTableCell(table, "40%");
        
        addTableCell(table, "Nilai Akhlak");
        addTableCell(table, String.format("%.2f", stats.getAvgAkhlak()));
        addTableCell(table, String.format("%.2f", stats.getMaxAkhlak()));
        addTableCell(table, "30%");
        
        addTableCell(table, "Nilai Ekstrakurikuler");
        addTableCell(table, String.format("%.2f", stats.getAvgEkstrakurikuler()));
        addTableCell(table, String.format("%.2f", stats.getMaxEkstrakurikuler()));
        addTableCell(table, "20%");
        
        addTableCell(table, "Nilai Absensi");
        addTableCell(table, String.format("%.2f", stats.getAvgAbsensi()));
        addTableCell(table, String.format("%.2f", stats.getMaxAbsensi()));
        addTableCell(table, "10%");
        
        document.add(table);
    }
    
    private void addPerformanceStatistics(Document document, PerformanceStatistics stats) throws DocumentException {
        Paragraph statsTitle = new Paragraph("STATISTIK PERFORMA KESELURUHAN", headerFont);
        statsTitle.setSpacingBefore(10);
        statsTitle.setSpacingAfter(10);
        document.add(statsTitle);
        
        PdfPTable statsTable = new PdfPTable(4);
        statsTable.setWidthPercentage(100);
        
        addStatsCell(statsTable, "Total Santri", String.valueOf(stats.getTotalSantri()), primaryColor);
        addStatsCell(statsTable, "Rata-rata Skor", df.format(stats.getAvgScore()), secondaryColor);
        addStatsCell(statsTable, "Skor Tertinggi", df.format(stats.getMaxScore()), BaseColor.ORANGE);
        addStatsCell(statsTable, "Skor Terendah", df.format(stats.getMinScore()), BaseColor.MAGENTA);
        
        document.add(statsTable);
        document.add(Chunk.NEWLINE);
    }
    
    private void addPerformersTable(Document document, String title, List<Penilaian> performers, BaseColor titleColor) throws DocumentException {
        Font coloredHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, titleColor);
        Paragraph tableTitle = new Paragraph(title, coloredHeaderFont);
        tableTitle.setSpacingBefore(15);
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15, 35, 25, 25});
        
        addTableHeader(table, "Rank");
        addTableHeader(table, "Nama Santri");
        addTableHeader(table, "Kelas");
        addTableHeader(table, "Skor SAW");
        
        for (Penilaian p : performers) {
            addTableCell(table, String.valueOf(p.getRanking()));
            addTableCell(table, p.getNamaSantri());
            addTableCell(table, p.getKelasSantri());
            addColoredTableCell(table, df.format(p.getSkorSAW()), titleColor);
        }
        
        document.add(table);
    }
    
    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(new BaseColor(240, 240, 240));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, contentFont));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private void addColoredTableCell(PdfPTable table, String text, BaseColor color) {
        Font coloredFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, color);
        PdfPCell cell = new PdfPCell(new Phrase(text, coloredFont));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private void addWeightDistribution(Document document) throws DocumentException {
        Paragraph weightTitle = new Paragraph("DISTRIBUSI BOBOT KRITERIA SAW", headerFont);
        weightTitle.setSpacingBefore(15);
        weightTitle.setSpacingAfter(10);
        document.add(weightTitle);
        
        Paragraph explanation = new Paragraph(
            "Sistem SAW menggunakan bobot yang telah ditentukan untuk setiap kriteria penilaian. " +
            "Nilai Raport memiliki bobot tertinggi (40%) karena merupakan indikator utama prestasi akademik. " +
            "Nilai Akhlak (30%) menunjukkan karakter santri, Ekstrakurikuler (20%) mengukur keterlibatan " +
            "dalam kegiatan non-akademik, dan Absensi (10%) mencerminkan kedisiplinan.",
            contentFont
        );
        explanation.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(explanation);
    }
    
    private void addClassSummaryStatistics(Document document, List<ClassStatistics> classStats) throws DocumentException {
        if (classStats.isEmpty()) return;
        
        Paragraph summaryTitle = new Paragraph("RINGKASAN ANALISIS", headerFont);
        summaryTitle.setSpacingBefore(15);
        summaryTitle.setSpacingAfter(10);
        document.add(summaryTitle);
        
        // Find best performing class
        ClassStatistics bestClass = classStats.stream()
            .max(Comparator.comparingDouble(ClassStatistics::getAvgScore))
            .orElse(null);
        
        // Find class with most achievers
        ClassStatistics mostAchievers = classStats.stream()
            .max(Comparator.comparingInt(ClassStatistics::getJumlahBerprestasi))
            .orElse(null);
        
        StringBuilder summary = new StringBuilder();
        if (bestClass != null) {
            summary.append("Kelas dengan rata-rata skor tertinggi: ")
                   .append(bestClass.getKelas())
                   .append(" (").append(df.format(bestClass.getAvgScore())).append("). ");
        }
        
        if (mostAchievers != null) {
            summary.append("Kelas dengan santri berprestasi terbanyak: ")
                   .append(mostAchievers.getKelas())
                   .append(" (").append(mostAchievers.getJumlahBerprestasi()).append(" santri).");
        }
        
        Paragraph summaryPara = new Paragraph(summary.toString(), contentFont);
        summaryPara.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(summaryPara);
    }
    
    private void addPDFFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        // Format tanggal dalam bahasa Indonesia
        LocalDateTime now = LocalDateTime.now();
        String[] days = {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", 
                          "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

        String dayName = days[now.getDayOfWeek().getValue() % 7];
        String monthName = months[now.getMonthValue() - 1];
        String formattedDate = String.format("Garut, %s %d %s %d", 
                                           dayName, now.getDayOfMonth(), monthName, now.getYear());

        // Bagian tanda tangan
        Paragraph dateLocation = new Paragraph(formattedDate, contentFont);
        dateLocation.setAlignment(Element.ALIGN_RIGHT);
        dateLocation.setSpacingAfter(10);
        document.add(dateLocation);

        Paragraph mengetahui = new Paragraph("Mengetahui             ", contentFont);
        mengetahui.setAlignment(Element.ALIGN_RIGHT);
        mengetahui.setSpacingAfter(60); 
        document.add(mengetahui);

        // Tanda kurung untuk nama
        Paragraph signature = new Paragraph("(                                     )", contentFont);
        signature.setAlignment(Element.ALIGN_RIGHT);
        signature.setSpacingAfter(20);
        document.add(signature);

        // Line separator sebelum footer
        document.add(new LineSeparator());

        // Footer asli
        Paragraph footer = new Paragraph(
            "Laporan ini dihasilkan secara otomatis oleh Sistem Informasi Santri Berprestasi menggunakan metode SAW (Simple Additive Weighting). " +
            "Tanggal cetak: " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            smallFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);
    }
    
    private String getPerformanceCategory(double score) {
        if (score >= 0.8) return "Sangat Baik";
        else if (score >= 0.6) return "Baik";
        else if (score >= 0.4) return "Cukup";
        else return "Perlu Perbaikan";
    }
    
    // Helper methods (unchanged)
    private ClassStatistics calculateClassStatistics(String kelas, List<Penilaian> kelasData) {
        int jumlahSantri = kelasData.size();
        double avgScore = kelasData.stream().mapToDouble(Penilaian::getSkorSAW).average().orElse(0);
        double maxScore = kelasData.stream().mapToDouble(Penilaian::getSkorSAW).max().orElse(0);
        double minScore = kelasData.stream().mapToDouble(Penilaian::getSkorSAW).min().orElse(0);
        int jumlahBerprestasi = (int) kelasData.stream().filter(p -> p.getRanking() <= 3).count();
        
        return new ClassStatistics(kelas, jumlahSantri, avgScore, maxScore, minScore, jumlahBerprestasi);
    }
    
    private CriteriaStatistics calculateCriteriaStatistics(List<Penilaian> penilaianList) {
        double avgRaport = penilaianList.stream().mapToDouble(Penilaian::getNilaiRaport).average().orElse(0);
        double avgAkhlak = penilaianList.stream().mapToDouble(Penilaian::getNilaiAkhlak).average().orElse(0);
        double avgEkstrakurikuler = penilaianList.stream().mapToDouble(Penilaian::getNilaiEkstrakurikuler).average().orElse(0);
        double avgAbsensi = penilaianList.stream().mapToDouble(Penilaian::getNilaiAbsensi).average().orElse(0);
        
        double maxRaport = penilaianList.stream().mapToDouble(Penilaian::getNilaiRaport).max().orElse(0);
        double maxAkhlak = penilaianList.stream().mapToDouble(Penilaian::getNilaiAkhlak).max().orElse(0);
        double maxEkstrakurikuler = penilaianList.stream().mapToDouble(Penilaian::getNilaiEkstrakurikuler).max().orElse(0);
        double maxAbsensi = penilaianList.stream().mapToDouble(Penilaian::getNilaiAbsensi).max().orElse(0);
        
        return new CriteriaStatistics(avgRaport, avgAkhlak, avgEkstrakurikuler, avgAbsensi,
                                    maxRaport, maxAkhlak, maxEkstrakurikuler, maxAbsensi);
    }
    
    private PerformanceStatistics calculatePerformanceStatistics(List<Penilaian> allPenilaian) {
        int totalSantri = allPenilaian.size();
        double avgScore = allPenilaian.stream().mapToDouble(Penilaian::getSkorSAW).average().orElse(0);
        double maxScore = allPenilaian.stream().mapToDouble(Penilaian::getSkorSAW).max().orElse(0);
        double minScore = allPenilaian.stream().mapToDouble(Penilaian::getSkorSAW).min().orElse(0);
        
        return new PerformanceStatistics(totalSantri, avgScore, maxScore, minScore);
    }
    
    private List<Penilaian> getLowPerformers(List<Penilaian> allPenilaian, int limit) {
        return allPenilaian.stream()
                .sorted(Comparator.comparingDouble(Penilaian::getSkorSAW))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    // Report Data Classes
    public static class RankingReport {
        private final List<Penilaian> rankings;
        private final String timestamp;
        
        public RankingReport(List<Penilaian> rankings, String timestamp) {
            this.rankings = rankings;
            this.timestamp = timestamp;
        }
        
        public List<Penilaian> getRankings() { return rankings; }
        public String getTimestamp() { return timestamp; }
    }
    
    public static class ClassComparisonReport {
        private final List<ClassStatistics> classStatistics;
        private final String timestamp;
        
        public ClassComparisonReport(List<ClassStatistics> classStatistics, String timestamp) {
            this.classStatistics = classStatistics;
            this.timestamp = timestamp;
        }
        
        public List<ClassStatistics> getClassStatistics() { return classStatistics; }
        public String getTimestamp() { return timestamp; }
    }
    
    public static class CriteriaAnalysisReport {
        private final CriteriaStatistics statistics;
        private final String timestamp;
        
        public CriteriaAnalysisReport(CriteriaStatistics statistics, String timestamp) {
            this.statistics = statistics;
            this.timestamp = timestamp;
        }
        
        public CriteriaStatistics getStatistics() { return statistics; }
        public String getTimestamp() { return timestamp; }
    }
    
    public static class PerformanceSummaryReport {
        private final List<Penilaian> topPerformers;
        private final List<Penilaian> lowPerformers;
        private final PerformanceStatistics statistics;
        private final String timestamp;
        
        public PerformanceSummaryReport(List<Penilaian> topPerformers, List<Penilaian> lowPerformers,
                                      PerformanceStatistics statistics, String timestamp) {
            this.topPerformers = topPerformers;
            this.lowPerformers = lowPerformers;
            this.statistics = statistics;
            this.timestamp = timestamp;
        }
        
        public List<Penilaian> getTopPerformers() { return topPerformers; }
        public List<Penilaian> getLowPerformers() { return lowPerformers; }
        public PerformanceStatistics getStatistics() { return statistics; }
        public String getTimestamp() { return timestamp; }
    }
    
    // Statistics Classes
    public static class ClassStatistics {
        private final String kelas;
        private final int jumlahSantri;
        private final double avgScore;
        private final double maxScore;
        private final double minScore;
        private final int jumlahBerprestasi;
        
        public ClassStatistics(String kelas, int jumlahSantri, double avgScore, 
                             double maxScore, double minScore, int jumlahBerprestasi) {
            this.kelas = kelas;
            this.jumlahSantri = jumlahSantri;
            this.avgScore = avgScore;
            this.maxScore = maxScore;
            this.minScore = minScore;
            this.jumlahBerprestasi = jumlahBerprestasi;
        }
        
        public String getKelas() { return kelas; }
        public int getJumlahSantri() { return jumlahSantri; }
        public double getAvgScore() { return avgScore; }
        public double getMaxScore() { return maxScore; }
        public double getMinScore() { return minScore; }
        public int getJumlahBerprestasi() { return jumlahBerprestasi; }
    }
    
    public static class CriteriaStatistics {
        private final double avgRaport, avgAkhlak, avgEkstrakurikuler, avgAbsensi;
        private final double maxRaport, maxAkhlak, maxEkstrakurikuler, maxAbsensi;
        
        public CriteriaStatistics(double avgRaport, double avgAkhlak, double avgEkstrakurikuler, 
                                double avgAbsensi, double maxRaport, double maxAkhlak, 
                                double maxEkstrakurikuler, double maxAbsensi) {
            this.avgRaport = avgRaport;
            this.avgAkhlak = avgAkhlak;
            this.avgEkstrakurikuler = avgEkstrakurikuler;
            this.avgAbsensi = avgAbsensi;
            this.maxRaport = maxRaport;
            this.maxAkhlak = maxAkhlak;
            this.maxEkstrakurikuler = maxEkstrakurikuler;
            this.maxAbsensi = maxAbsensi;
        }
        
        public double getAvgRaport() { return avgRaport; }
        public double getAvgAkhlak() { return avgAkhlak; }
        public double getAvgEkstrakurikuler() { return avgEkstrakurikuler; }
        public double getAvgAbsensi() { return avgAbsensi; }
        public double getMaxRaport() { return maxRaport; }
        public double getMaxAkhlak() { return maxAkhlak; }
        public double getMaxEkstrakurikuler() { return maxEkstrakurikuler; }
        public double getMaxAbsensi() { return maxAbsensi; }
    }
    
    public static class PerformanceStatistics {
        private final int totalSantri;
        private final double avgScore;
        private final double maxScore;
        private final double minScore;
        
        public PerformanceStatistics(int totalSantri, double avgScore, double maxScore, double minScore) {
            this.totalSantri = totalSantri;
            this.avgScore = avgScore;
            this.maxScore = maxScore;
            this.minScore = minScore;
        }
        
        public int getTotalSantri() { return totalSantri; }
        public double getAvgScore() { return avgScore; }
        public double getMaxScore() { return maxScore; }
        public double getMinScore() { return minScore; }
    }
}