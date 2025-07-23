// src/main/java/com/skripsi/view/ReportView.java
package com.skripsi.view;

import com.skripsi.controller.ReportController;
import com.skripsi.model.Penilaian;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.concurrent.Task;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class ReportView extends Application {
    
    private ReportController reportController;
    private BorderPane mainLayout;
    private TabPane reportTabPane;
    private Stage primaryStage;
    private DecimalFormat df = new DecimalFormat("#.####");
    private File selectedFile;
    
    public ReportView() {
        this.reportController = new ReportController();
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Laporan SAW - Sistem Santri Berprestasi");
        
        createMainLayout();
        setupReportTabs();
        
        Scene scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add("data:text/css," + 
            ".root { -fx-font-family: 'Arial', sans-serif; }"
        );
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        startAnimation();
    }
    
    private void createMainLayout() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f7fafc;");
        
        createHeader();
        createContentArea();
    }
    
    private void createHeader() {
        VBox headerSection = new VBox(20);
        headerSection.setPadding(new Insets(30));
        
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, null,
            new Stop(0, Color.web("#667eea")),
            new Stop(1, Color.web("#764ba2"))
        );
        BackgroundFill bgFill = new BackgroundFill(gradient, new CornerRadii(0), Insets.EMPTY);
        headerSection.setBackground(new Background(bgFill));
        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label headerLabel = new Label("📊 Sistem Laporan SAW");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        headerLabel.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button backBtn = createHeaderButton("⬅ Kembali");
        Button refreshBtn = createHeaderButton("🔄 Refresh");
        
        backBtn.setOnAction(e -> primaryStage.close());
        refreshBtn.setOnAction(e -> refreshAllReports());
        
        headerBox.getChildren().addAll(headerLabel, spacer, backBtn, refreshBtn);
        headerSection.getChildren().add(headerBox);
        
        mainLayout.setTop(headerSection);
    }
    
    private void createContentArea() {
        reportTabPane = new TabPane();
        reportTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        reportTabPane.setPadding(new Insets(20));
        mainLayout.setCenter(reportTabPane);
    }
    
    private void setupReportTabs() {
        Tab rankingTab = new Tab("🏆 Laporan Ranking");
        rankingTab.setContent(createRankingReportContent());
        
        Tab classTab = new Tab("📚 Perbandingan Kelas");
        classTab.setContent(createClassComparisonContent());
        
        Tab criteriaTab = new Tab("📈 Analisis Kriteria");
        criteriaTab.setContent(createCriteriaAnalysisContent());
        
        Tab performanceTab = new Tab("📋 Ringkasan Performa");
        performanceTab.setContent(createPerformanceSummaryContent());
        
        reportTabPane.getTabs().addAll(rankingTab, classTab, criteriaTab, performanceTab);
    }
    
    private ScrollPane createRankingReportContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        HBox headerBox = createReportHeader("Laporan Ranking Santri Berprestasi", 
            e -> exportRankingReport());
        
        HBox statsCards = createRankingStatsCards();
        VBox tableSection = createRankingTableSection();
        
        content.getChildren().addAll(headerBox, statsCards, tableSection);
        
        return createScrollPane(content);
    }
    
    private HBox createRankingStatsCards() {
        ReportController.RankingReport report = reportController.generateRankingReport();
        List<Penilaian> rankings = report.getRankings();
        
        int total = rankings.size();
        int berprestasi = (int) rankings.stream().filter(p -> p.getRanking() <= 3).count();
        int baik = (int) rankings.stream().filter(p -> p.getRanking() > 3 && p.getRanking() <= 10).count();
        int perluDitingkatkan = total - berprestasi - baik;
        
        return createStatsBox(
            createStatsCard("👥", "Total Santri", String.valueOf(total), "#4299e1"),
            createStatsCard("🏆", "Berprestasi", String.valueOf(berprestasi), "#48bb78"),
            createStatsCard("⭐", "Baik", String.valueOf(baik), "#ed8936"),
            createStatsCard("📚", "Perlu Ditingkatkan", String.valueOf(perluDitingkatkan), "#f56565")
        );
    }
    
    private VBox createRankingTableSection() {
        VBox tableSection = createTableContainer("Tabel Ranking Lengkap");
        TableView<Penilaian> table = createRankingTable();
        tableSection.getChildren().add(table);
        return tableSection;
    }
    
    @SuppressWarnings("unchecked")
    private TableView<Penilaian> createRankingTable() {
        TableView<Penilaian> table = new TableView<>();
        table.setPrefHeight(400);
        
        TableColumn<Penilaian, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("ranking"));
        rankCol.setPrefWidth(60);
        
        TableColumn<Penilaian, String> namaCol = new TableColumn<>("Nama Santri");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("namaSantri"));
        namaCol.setPrefWidth(180);
        
        TableColumn<Penilaian, String> kelasCol = new TableColumn<>("Kelas");
        kelasCol.setCellValueFactory(new PropertyValueFactory<>("kelasSantri"));
        kelasCol.setPrefWidth(100);
        
        TableColumn<Penilaian, Double> skorCol = new TableColumn<>("Skor SAW");
        skorCol.setCellValueFactory(new PropertyValueFactory<>("skorSAW"));
        skorCol.setPrefWidth(120);
        skorCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #4299e1;");
                }
            }
        });
        
        TableColumn<Penilaian, Integer> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("ranking"));
        statusCol.setPrefWidth(150);
        statusCol.setCellFactory(column -> new TableCell<Penilaian, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item <= 3) {
                        setText("🏆 Berprestasi");
                        setStyle("-fx-text-fill: #48bb78; -fx-font-weight: bold;");
                    } else if (item <= 10) {
                        setText("⭐ Baik");
                        setStyle("-fx-text-fill: #4299e1; -fx-font-weight: bold;");
                    } else {
                        setText("📚 Perlu Ditingkatkan");
                        setStyle("-fx-text-fill: #ed8936;");
                    }
                }
            }
        });
        
        table.getColumns().addAll(rankCol, namaCol, kelasCol, skorCol, statusCol);
        
        ReportController.RankingReport report = reportController.generateRankingReport();
        ObservableList<Penilaian> data = FXCollections.observableArrayList(report.getRankings());
        table.setItems(data);
        
        return table;
    }
    
    private ScrollPane createClassComparisonContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        HBox headerBox = createReportHeader("Laporan Perbandingan Antar Kelas", 
            e -> exportClassComparisonReport());
        
        HBox chartSection = createClassComparisonCharts();
        VBox tableSection = createClassComparisonTable();
        
        content.getChildren().addAll(headerBox, chartSection, tableSection);
        
        return createScrollPane(content);
    }
    
    private HBox createClassComparisonCharts() {
        HBox chartBox = new HBox(20);
        chartBox.setAlignment(Pos.CENTER);
        
        BarChart<String, Number> avgChart = createClassAverageChart();
        PieChart performanceChart = createPerformanceDistributionChart();
        
        chartBox.getChildren().addAll(avgChart, performanceChart);
        return chartBox;
    }
    
    private BarChart<String, Number> createClassAverageChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Kelas");
        yAxis.setLabel("Rata-rata Skor SAW");
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Rata-rata Skor SAW per Kelas");
        chart.setPrefWidth(400);
        chart.setPrefHeight(300);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Rata-rata Skor");
        
        ReportController.ClassComparisonReport report = reportController.generateClassComparisonReport();
        for (ReportController.ClassStatistics stats : report.getClassStatistics()) {
            series.getData().add(new XYChart.Data<>(stats.getKelas(), stats.getAvgScore()));
        }
        
        chart.getData().add(series);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        return chart;
    }
    
    private PieChart createPerformanceDistributionChart() {
        PieChart chart = new PieChart();
        chart.setTitle("Distribusi Performa Santri");
        chart.setPrefWidth(400);
        chart.setPrefHeight(300);
        
        ReportController.RankingReport rankingReport = reportController.generateRankingReport();
        List<Penilaian> rankings = rankingReport.getRankings();
        
        int berprestasi = (int) rankings.stream().filter(p -> p.getRanking() <= 3).count();
        int baik = (int) rankings.stream().filter(p -> p.getRanking() > 3 && p.getRanking() <= 10).count();
        int perluDitingkatkan = rankings.size() - berprestasi - baik;
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Berprestasi", berprestasi),
            new PieChart.Data("Baik", baik),
            new PieChart.Data("Perlu Ditingkatkan", perluDitingkatkan)
        );
        
        chart.setData(pieChartData);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        return chart;
    }
    
    @SuppressWarnings("unchecked")
    private VBox createClassComparisonTable() {
        VBox tableSection = createTableContainer("Detail Perbandingan Kelas");
        
        TableView<ReportController.ClassStatistics> table = new TableView<>();
        table.setPrefHeight(300);
        
        TableColumn<ReportController.ClassStatistics, String> kelasCol = new TableColumn<>("Kelas");
        kelasCol.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        kelasCol.setPrefWidth(100);
        
        TableColumn<ReportController.ClassStatistics, Integer> jumlahCol = new TableColumn<>("Jumlah Santri");
        jumlahCol.setCellValueFactory(new PropertyValueFactory<>("jumlahSantri"));
        jumlahCol.setPrefWidth(120);
        
        TableColumn<ReportController.ClassStatistics, Double> avgCol = new TableColumn<>("Rata-rata Skor");
        avgCol.setCellValueFactory(new PropertyValueFactory<>("avgScore"));
        avgCol.setPrefWidth(120);
        avgCol.setCellFactory(column -> new TableCell<ReportController.ClassStatistics, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : df.format(item));
            }
        });
        
        TableColumn<ReportController.ClassStatistics, Double> maxCol = new TableColumn<>("Skor Tertinggi");
        maxCol.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
        maxCol.setPrefWidth(120);
        maxCol.setCellFactory(column -> new TableCell<ReportController.ClassStatistics, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : df.format(item));
            }
        });
        
        TableColumn<ReportController.ClassStatistics, Integer> berprestasiCol = new TableColumn<>("Jumlah Berprestasi");
        berprestasiCol.setCellValueFactory(new PropertyValueFactory<>("jumlahBerprestasi"));
        berprestasiCol.setPrefWidth(150);
        
        table.getColumns().addAll(kelasCol, jumlahCol, avgCol, maxCol, berprestasiCol);
        
        ReportController.ClassComparisonReport report = reportController.generateClassComparisonReport();
        ObservableList<ReportController.ClassStatistics> data = FXCollections.observableArrayList(report.getClassStatistics());
        table.setItems(data);
        
        tableSection.getChildren().add(table);
        return tableSection;
    }
    
    private ScrollPane createCriteriaAnalysisContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        HBox headerBox = createReportHeader("Laporan Analisis Kriteria Penilaian", 
            e -> exportCriteriaAnalysisReport());
        
        VBox criteriaStats = createCriteriaStatsSection();
        HBox criteriaCharts = createCriteriaChartsSection();
        
        content.getChildren().addAll(headerBox, criteriaStats, criteriaCharts);
        
        return createScrollPane(content);
    }
    
    private VBox createCriteriaStatsSection() {
        VBox statsSection = createTableContainer("Statistik Kriteria Penilaian");
        
        ReportController.CriteriaAnalysisReport report = reportController.generateCriteriaAnalysisReport();
        ReportController.CriteriaStatistics stats = report.getStatistics();
        
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        
        // Headers
        grid.add(createHeaderLabel("Kriteria"), 0, 0);
        grid.add(createHeaderLabel("Rata-rata"), 1, 0);
        grid.add(createHeaderLabel("Nilai Tertinggi"), 2, 0);
        grid.add(createHeaderLabel("Bobot SAW"), 3, 0);
        
        // Data rows
        String[][] criteriaData = {
            {"📚 Nilai Raport", String.format("%.2f", stats.getAvgRaport()), String.format("%.2f", stats.getMaxRaport()), "40%"},
            {"🤲 Nilai Akhlak", String.format("%.2f", stats.getAvgAkhlak()), String.format("%.2f", stats.getMaxAkhlak()), "30%"},
            {"🏃 Ekstrakurikuler", String.format("%.2f", stats.getAvgEkstrakurikuler()), String.format("%.2f", stats.getMaxEkstrakurikuler()), "20%"},
            {"📅 Nilai Absensi", String.format("%.2f", stats.getAvgAbsensi()), String.format("%.2f", stats.getMaxAbsensi()), "10%"}
        };
        
        for (int i = 0; i < criteriaData.length; i++) {
            grid.add(createCriteriaLabel(criteriaData[i][0]), 0, i + 1);
            grid.add(createValueLabel(criteriaData[i][1]), 1, i + 1);
            grid.add(createValueLabel(criteriaData[i][2]), 2, i + 1);
            grid.add(createValueLabel(criteriaData[i][3]), 3, i + 1);
        }
        
        statsSection.getChildren().add(grid);
        return statsSection;
    }
    
    private HBox createCriteriaChartsSection() {
        HBox chartBox = new HBox(20);
        chartBox.setAlignment(Pos.CENTER);
        
        BarChart<String, Number> avgChart = createCriteriaAverageChart();
        PieChart weightChart = createWeightDistributionChart();
        
        chartBox.getChildren().addAll(avgChart, weightChart);
        return chartBox;
    }
    
    private BarChart<String, Number> createCriteriaAverageChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Kriteria");
        yAxis.setLabel("Rata-rata Nilai");
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Rata-rata Nilai per Kriteria");
        chart.setPrefWidth(400);
        chart.setPrefHeight(300);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Rata-rata");
        
        ReportController.CriteriaAnalysisReport report = reportController.generateCriteriaAnalysisReport();
        ReportController.CriteriaStatistics stats = report.getStatistics();
        
        series.getData().add(new XYChart.Data<>("Raport", stats.getAvgRaport()));
        series.getData().add(new XYChart.Data<>("Akhlak", stats.getAvgAkhlak()));
        series.getData().add(new XYChart.Data<>("Ekskul", stats.getAvgEkstrakurikuler()));
        series.getData().add(new XYChart.Data<>("Absensi", stats.getAvgAbsensi()));
        
        chart.getData().add(series);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        return chart;
    }
    
    private PieChart createWeightDistributionChart() {
        PieChart chart = new PieChart();
        chart.setTitle("Distribusi Bobot Kriteria SAW");
        chart.setPrefWidth(400);
        chart.setPrefHeight(300);
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Raport (40%)", 40),
            new PieChart.Data("Akhlak (30%)", 30),
            new PieChart.Data("Ekstrakurikuler (20%)", 20),
            new PieChart.Data("Absensi (10%)", 10)
        );
        
        chart.setData(pieChartData);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        return chart;
    }
    
    private ScrollPane createPerformanceSummaryContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        HBox headerBox = createReportHeader("Laporan Ringkasan Performa Santri", 
            e -> exportPerformanceSummaryReport());
        
        HBox performanceStats = createPerformanceStatsCards();
        HBox performersSection = createPerformersSection();
        
        content.getChildren().addAll(headerBox, performanceStats, performersSection);
        
        return createScrollPane(content);
    }
    
    private HBox createPerformanceStatsCards() {
        ReportController.PerformanceSummaryReport report = reportController.generatePerformanceSummaryReport();
        ReportController.PerformanceStatistics stats = report.getStatistics();
        
        return createStatsBox(
            createStatsCard("👥", "Total Santri", String.valueOf(stats.getTotalSantri()), "#4299e1"),
            createStatsCard("📈", "Rata-rata Skor", df.format(stats.getAvgScore()), "#48bb78"),
            createStatsCard("🏆", "Skor Tertinggi", df.format(stats.getMaxScore()), "#ed8936"),
            createStatsCard("📊", "Skor Terendah", df.format(stats.getMinScore()), "#9f7aea")
        );
    }
    
    private HBox createPerformersSection() {
        HBox performersBox = new HBox(20);
        performersBox.setAlignment(Pos.TOP_CENTER);
        
        VBox topSection = createTopPerformersSection();
        VBox lowSection = createLowPerformersSection();
        
        performersBox.getChildren().addAll(topSection, lowSection);
        return performersBox;
    }
    
    @SuppressWarnings("unchecked")
    private VBox createTopPerformersSection() {
        VBox section = createPerformerTableContainer("🏆 Top 5 Performers");
        
        TableView<Penilaian> table = createPerformerTable();
        
        // Apply green styling for top performers' scores
        TableColumn<Penilaian, Double> skorCol = (TableColumn<Penilaian, Double>) table.getColumns().get(3);
        skorCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #48bb78;");
                }
            }
        });
        
        ReportController.PerformanceSummaryReport report = reportController.generatePerformanceSummaryReport();
        ObservableList<Penilaian> data = FXCollections.observableArrayList(report.getTopPerformers());
        table.setItems(data);
        
        section.getChildren().add(table);
        return section;
    }
    
    @SuppressWarnings("unchecked")
    private VBox createLowPerformersSection() {
        VBox section = createPerformerTableContainer("📚 Perlu Perhatian Khusus");
        
        TableView<Penilaian> table = createPerformerTable();
        
        // Apply orange styling for low performers' scores
        TableColumn<Penilaian, Double> skorCol = (TableColumn<Penilaian, Double>) table.getColumns().get(3);
        skorCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #ed8936;");
                }
            }
        });
        
        ReportController.PerformanceSummaryReport report = reportController.generatePerformanceSummaryReport();
        ObservableList<Penilaian> data = FXCollections.observableArrayList(report.getLowPerformers());
        table.setItems(data);
        
        section.getChildren().add(table);
        return section;
    }
    
    // PDF Export methods with progress dialog
    private void exportRankingReport() {
        exportToPDF("Export Laporan Ranking", "laporan_ranking_", 
            () -> reportController.exportRankingReportToPDF(getSelectedFile().getAbsolutePath()));
    }
    
    private void exportClassComparisonReport() {
        exportToPDF("Export Laporan Perbandingan Kelas", "laporan_perbandingan_kelas_", 
            () -> reportController.exportClassComparisonToPDF(getSelectedFile().getAbsolutePath()));
    }
    
    private void exportCriteriaAnalysisReport() {
        exportToPDF("Export Laporan Analisis Kriteria", "laporan_analisis_kriteria_", 
            () -> reportController.exportCriteriaAnalysisToPDF(getSelectedFile().getAbsolutePath()));
    }
    
    private void exportPerformanceSummaryReport() {
        exportToPDF("Export Laporan Ringkasan Performa", "laporan_ringkasan_performa_", 
            () -> reportController.exportPerformanceSummaryToPDF(getSelectedFile().getAbsolutePath()));
    }
    
    private void exportToPDF(String title, String fileName, java.util.function.Supplier<Boolean> exportFunction) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName(fileName + java.time.LocalDate.now().toString() + ".pdf");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        selectedFile = fileChooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            // Show progress dialog
            Alert progressAlert = createProgressAlert("Mengekspor Laporan", "Sedang membuat file PDF...");
            progressAlert.show();
            
            // Create background task for PDF generation
            Task<Boolean> exportTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return exportFunction.get();
                }
                
                @Override
                protected void succeeded() {
                    progressAlert.close();
                    if (getValue()) {
                        showSuccessAlert("Export Berhasil", 
                            "Laporan PDF berhasil dibuat!\n\nLokasi file: " + selectedFile.getAbsolutePath() + 
                            "\n\nUkuran file: " + formatFileSize(selectedFile.length()));
                        
                        // Ask if user wants to open the file
                        if (showConfirmDialog("Buka File", "Apakah Anda ingin membuka file PDF yang baru dibuat?")) {
                            openPDFFile(selectedFile);
                        }
                    } else {
                        showErrorAlert("Export Gagal", 
                            "Gagal mengekspor laporan ke PDF.\n\nPastikan:\n" +
                            "- Lokasi penyimpanan dapat diakses\n" +
                            "- File tidak sedang dibuka di aplikasi lain\n" +
                            "- Ruang disk mencukupi");
                    }
                }
                
                @Override
                protected void failed() {
                    progressAlert.close();
                    showErrorAlert("Export Error", 
                        "Terjadi kesalahan saat mengekspor laporan:\n" + getException().getMessage());
                }
            };
            
            // Run the task in background thread
            new Thread(exportTask).start();
        }
    }
    
    private Alert createProgressAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Remove default buttons and add progress indicator
        alert.getDialogPane().getButtonTypes().clear();
        
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px;");
        
        content.getChildren().addAll(progressIndicator, messageLabel);
        alert.getDialogPane().setContent(content);
        
        return alert;
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    private boolean showConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        ButtonType yesButton = new ButtonType("Ya", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Tidak", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);
        
        return alert.showAndWait().orElse(noButton) == yesButton;
    }
    
    private void openPDFFile(File file) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(file);
            } else {
                showInfoAlert("File Tersimpan", "File PDF telah disimpan di: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showInfoAlert("File Tersimpan", 
                "File PDF telah disimpan di: " + file.getAbsolutePath() + 
                "\n\nSilakan buka file secara manual.");
        }
    }
    
    // UI Helper methods
    private HBox createReportHeader(String title, javafx.event.EventHandler<javafx.event.ActionEvent> exportAction) {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#2d3748"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button exportBtn = createExportButton("📄 Export PDF");
        exportBtn.setOnAction(exportAction);
        
        headerBox.getChildren().addAll(titleLabel, spacer, exportBtn);
        return headerBox;
    }
    
    private HBox createStatsBox(VBox... cards) {
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.getChildren().addAll(cards);
        return statsBox;
    }
    
    private VBox createStatsCard(String icon, String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPrefWidth(200);
        card.setPrefHeight(120);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 24));
        iconLabel.setTextFill(Color.web(color));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.web("#2d3748"));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(Color.web("#718096"));
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }
    
    private VBox createTableContainer(String title) {
        VBox tableSection = new VBox(15);
        tableSection.setPadding(new Insets(25));
        tableSection.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        Label tableTitle = new Label(title);
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tableTitle.setTextFill(Color.web("#2d3748"));
        
        tableSection.getChildren().add(tableTitle);
        return tableSection;
    }
    
    private VBox createPerformerTableContainer(String title) {
        VBox section = new VBox(15);
        section.setPrefWidth(580);
        section.setPadding(new Insets(25));
        section.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#2d3748"));
        
        section.getChildren().add(titleLabel);
        return section;
    }
    
    @SuppressWarnings("unchecked")
    private TableView<Penilaian> createPerformerTable() {
        TableView<Penilaian> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<Penilaian, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("ranking"));
        rankCol.setPrefWidth(60);
        
        TableColumn<Penilaian, String> namaCol = new TableColumn<>("Nama");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("namaSantri"));
        namaCol.setPrefWidth(200);
        
        TableColumn<Penilaian, String> kelasCol = new TableColumn<>("Kelas");
        kelasCol.setCellValueFactory(new PropertyValueFactory<>("kelasSantri"));
        kelasCol.setPrefWidth(80);
        
        TableColumn<Penilaian, Double> skorCol = new TableColumn<>("Skor");
        skorCol.setCellValueFactory(new PropertyValueFactory<>("skorSAW"));
        skorCol.setPrefWidth(100);
        
        table.getColumns().addAll(rankCol, namaCol, kelasCol, skorCol);
        return table;
    }
    
    private ScrollPane createScrollPane(VBox content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scrollPane;
    }
    
    private Button createHeaderButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.web("#667eea"));
        button.setPrefHeight(40);
        button.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 16 8 16;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: #f7fafc;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16 8 16;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16 8 16;"
            );
        });
        
        return button;
    }
    
    private Button createExportButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setPrefHeight(40);
        button.setTextFill(Color.WHITE);
        button.setStyle(
            "-fx-background-color: #e53e3e;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 16 8 16;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: derive(#e53e3e, -10%);" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: #e53e3e;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        return button;
    }
    
    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.web("#2d3748"));
        label.setPrefWidth(120);
        return label;
    }
    
    private Label createCriteriaLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setTextFill(Color.web("#4a5568"));
        label.setPrefWidth(150);
        return label;
    }
    
    private Label createValueLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.web("#4299e1"));
        label.setPrefWidth(100);
        label.setAlignment(Pos.CENTER);
        return label;
    }
    
    private File getSelectedFile() {
        return selectedFile;
    }
    
    private void refreshAllReports() {
        reportTabPane.getTabs().clear();
        setupReportTabs();
        showSuccessAlert("Refresh Berhasil", "Semua laporan telah diperbarui dengan data terbaru.");
    }
    
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Custom styling for success alert
        alert.getDialogPane().setStyle("-fx-background-color: #f0fff4;");
        alert.showAndWait();
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Custom styling for error alert
        alert.getDialogPane().setStyle("-fx-background-color: #fff5f5;");
        alert.showAndWait();
    }
    
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void startAnimation() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainLayout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}