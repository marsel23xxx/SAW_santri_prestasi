// src/main/java/com/skripsi/view/SAWResultView.java - FIXED VERSION
package com.skripsi.view;

import com.skripsi.controller.SAWResultController;
import com.skripsi.service.SAWCalculationService;
import com.skripsi.model.Penilaian;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.List;

public class SAWResultView extends Application {
    
    private SAWResultController sawController;
    private BorderPane mainLayout;
    private TableView<Penilaian> rankingTable;
    private ObservableList<Penilaian> rankingData;
    private ComboBox<String> kelasFilter;
    private Label totalSantriLabel, avgScoreLabel, maxScoreLabel;
    private VBox topThreeSection;
    private HBox statsBox;
    
    public SAWResultView() {
        this.sawController = new SAWResultController();
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hasil SAW - Ranking Santri Berprestasi");
        
        createMainLayout();
        setupTable();
        loadData();
        
        // FIXED: Match dashboard window settings
        Scene scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add("data:text/css," + 
            ".root { -fx-font-family: 'Arial', sans-serif; }" +
            ".stats-card { -fx-background-color: white; -fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3); " +
            "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 15; }"
        );
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // FIXED: Start maximized like dashboard
        primaryStage.show();
        
        startAnimation();
    }
    
    private void createMainLayout() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f7fafc;"); // Match dashboard background
        
        // Top section with gradient like dashboard
        createTopSection();
        
        // Content section
        createContentSection();
    }
    
    private void createTopSection() {
        VBox topSection = new VBox(20);
        topSection.setPadding(new Insets(30));
        
        // FIXED: Use same gradient as dashboard sidebar
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, null,
            new Stop(0, Color.web("#667eea")),
            new Stop(1, Color.web("#764ba2"))
        );
        BackgroundFill bgFill = new BackgroundFill(gradient, new CornerRadii(0), Insets.EMPTY);
        topSection.setBackground(new Background(bgFill));
        
        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label headerLabel = new Label("🏆 Hasil Perhitungan SAW");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        headerLabel.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button recalculateBtn = createHeaderButton("🔄 Hitung Ulang");
        Button exportBtn = createHeaderButton("📊 Export");
        Button backBtn = createHeaderButton("⬅ Kembali");
        
        recalculateBtn.setOnAction(e -> recalculateSAW());
        exportBtn.setOnAction(e -> showInfo("Fitur export akan segera hadir!"));
        backBtn.setOnAction(e -> ((Stage) mainLayout.getScene().getWindow()).close());
        
        headerBox.getChildren().addAll(headerLabel, spacer, backBtn, recalculateBtn, exportBtn);
        
        // Stats cards with dashboard styling
        statsBox = createStatsSection();
        
        topSection.getChildren().addAll(headerBox, statsBox);
        mainLayout.setTop(topSection);
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
            "-fx-padding: 8 16 8 16;" +
            "-fx-margin: 0 5 0 0;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: #f7fafc;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        return button;
    }
    
    private HBox createStatsSection() {
        HBox statsContainer = new HBox(30);
        statsContainer.setAlignment(Pos.CENTER);
        
        VBox totalCard = createStatsCard("📊", "Total Santri", "0", "santri dinilai");
        VBox avgCard = createStatsCard("📈", "Rata-rata Skor", "0.0000", "skor SAW");
        VBox maxCard = createStatsCard("🏆", "Skor Tertinggi", "0.0000", "pencapaian terbaik");
        
        // Store references to the value labels for updates
        totalSantriLabel = (Label) totalCard.getChildren().get(1);
        avgScoreLabel = (Label) avgCard.getChildren().get(1);
        maxScoreLabel = (Label) maxCard.getChildren().get(1);
        
        statsContainer.getChildren().addAll(totalCard, avgCard, maxCard);
        return statsContainer;
    }
    
    private VBox createStatsCard(String icon, String title, String value, String subtitle) {
        VBox card = new VBox(10);
        
        // FIXED: Match dashboard stats card sizing
        card.setPrefWidth(220);
        card.setMinWidth(220);
        card.setMaxWidth(220);
        card.setPrefHeight(120);
        card.setMinHeight(120);
        card.setMaxHeight(120);
        
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        
        // FIXED: Use dashboard card styling
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        card.getStyleClass().add("stats-card");
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 24));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.web("#2d3748"));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web("#4a5568"));
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", 12));
        subtitleLabel.setTextFill(Color.web("#718096"));
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel, subtitleLabel);
        
        // Enhanced hover effect like dashboard
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.6, 0, 5);" +
                "-fx-border-color: #4299e1;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
                "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        return card;
    }
    
    private void createContentSection() {
        VBox contentSection = new VBox(30);
        contentSection.setPadding(new Insets(30));
        
        // Top 3 section
        createTopThreeSection();
        
        // Filter and table section
        VBox tableSection = createTableSection();
        
        contentSection.getChildren().addAll(topThreeSection, tableSection);
        
        ScrollPane scrollPane = new ScrollPane(contentSection);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        mainLayout.setCenter(scrollPane);
    }
    
    private void createTopThreeSection() {
        topThreeSection = new VBox(20);
        
        Label topThreeTitle = new Label("🥇 Top 3 Santri Berprestasi");
        topThreeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        topThreeTitle.setTextFill(Color.web("#2d3748"));
        
        HBox podiumBox = new HBox(20);
        podiumBox.setAlignment(Pos.CENTER);
        
        topThreeSection.getChildren().addAll(topThreeTitle, podiumBox);
    }
    
    private VBox createPodiumCard(int rank, Penilaian penilaian) {
        VBox card = new VBox(15);
        card.setPrefWidth(280); // Slightly larger to match dashboard proportions
        card.setPrefHeight(200);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER);
        
        String[] colors = {"#ffd700", "#c0c0c0", "#cd7f32"}; // Gold, Silver, Bronze
        String[] medals = {"🥇", "🥈", "🥉"};
        
        // Enhanced styling to match dashboard cards
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 8);" +
            "-fx-border-color: " + colors[rank - 1] + ";" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 20;"
        );
        
        Label medalLabel = new Label(medals[rank - 1]);
        medalLabel.setFont(Font.font("Arial", 40));
        
        Label rankLabel = new Label("Peringkat " + rank);
        rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        rankLabel.setTextFill(Color.web(colors[rank - 1]));
        
        Label nameLabel = new Label(penilaian.getNamaSantri());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web("#2d3748"));
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(220);
        
        Label kelasLabel = new Label(penilaian.getKelasSantri());
        kelasLabel.setFont(Font.font("Arial", 14));
        kelasLabel.setTextFill(Color.web("#718096"));
        
        DecimalFormat df = new DecimalFormat("#.####");
        Label scoreLabel = new Label("Skor: " + df.format(penilaian.getSkorSAW()));
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        scoreLabel.setTextFill(Color.web("#4299e1"));
        
        card.getChildren().addAll(medalLabel, rankLabel, nameLabel, kelasLabel, scoreLabel);
        
        // Enhanced hover animation
        card.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
            
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.6, 0, 10);" +
                "-fx-border-color: " + colors[rank - 1] + ";" +
                "-fx-border-width: 4;" +
                "-fx-border-radius: 20;"
            );
        });
        
        card.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 8);" +
                "-fx-border-color: " + colors[rank - 1] + ";" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 20;"
            );
        });
        
        return card;
    }
    
    private VBox createTableSection() {
        VBox tableSection = new VBox(20);
        tableSection.setPadding(new Insets(25));
        
        // Match dashboard table section styling
        tableSection.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 5);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        // Table header with filter
        HBox tableHeader = createTableHeader();
        
        // Create table
        rankingTable = new TableView<>();
        rankingTable.setPrefHeight(450); // Increased height for better visibility
        
        tableSection.getChildren().addAll(tableHeader, rankingTable);
        return tableSection;
    }
    
    private HBox createTableHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label tableTitle = new Label("📋 Ranking Lengkap Santri");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        tableTitle.setTextFill(Color.web("#2d3748"));
        
        Region spacer1 = new Region();
        
        Label filterLabel = new Label("Filter Kelas:");
        filterLabel.setFont(Font.font("Arial", 14));
        filterLabel.setTextFill(Color.web("#4a5568"));
        
        kelasFilter = new ComboBox<>();
        kelasFilter.setPrefWidth(150);
        kelasFilter.setPromptText("Semua Kelas");
        kelasFilter.setOnAction(e -> applyFilter());
        
        Button refreshBtn = createStyledButton("🔄 Refresh", "#48bb78");
        refreshBtn.setOnAction(e -> loadData());
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        header.getChildren().addAll(tableTitle, spacer1, filterLabel, kelasFilter, refreshBtn);
        return header;
    }
    
    @SuppressWarnings("unchecked")
    private void setupTable() {
        // Ranking column
        TableColumn<Penilaian, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("ranking"));
        rankCol.setPrefWidth(60);
        rankCol.setCellFactory(column -> new TableCell<Penilaian, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    // Color code top 3
                    if (item == 1) {
                        setStyle("-fx-background-color: #ffd70030; -fx-text-fill: #b8860b; -fx-font-weight: bold;");
                    } else if (item == 2) {
                        setStyle("-fx-background-color: #c0c0c030; -fx-text-fill: #696969; -fx-font-weight: bold;");
                    } else if (item == 3) {
                        setStyle("-fx-background-color: #cd7f3230; -fx-text-fill: #8b4513; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        // Name column
        TableColumn<Penilaian, String> namaCol = new TableColumn<>("Nama Santri");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("namaSantri"));
        namaCol.setPrefWidth(200); // Increased width for better readability
        
        // Class column
        TableColumn<Penilaian, String> kelasCol = new TableColumn<>("Kelas");
        kelasCol.setCellValueFactory(new PropertyValueFactory<>("kelasSantri"));
        kelasCol.setPrefWidth(100);
        
        // Criteria columns
        TableColumn<Penilaian, Double> raportCol = new TableColumn<>("Raport");
        raportCol.setCellValueFactory(new PropertyValueFactory<>("nilaiRaport"));
        raportCol.setPrefWidth(80);
        raportCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });
        
        TableColumn<Penilaian, Double> akhlakCol = new TableColumn<>("Akhlak");
        akhlakCol.setCellValueFactory(new PropertyValueFactory<>("nilaiAkhlak"));
        akhlakCol.setPrefWidth(80);
        akhlakCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });
        
        TableColumn<Penilaian, Double> ekskulCol = new TableColumn<>("Ekskul");
        ekskulCol.setCellValueFactory(new PropertyValueFactory<>("nilaiEkstrakurikuler"));
        ekskulCol.setPrefWidth(80);
        ekskulCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });
        
        TableColumn<Penilaian, Double> absensiCol = new TableColumn<>("Absensi");
        absensiCol.setCellValueFactory(new PropertyValueFactory<>("nilaiAbsensi"));
        absensiCol.setPrefWidth(80);
        absensiCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });
        
        // SAW Score column
        TableColumn<Penilaian, Double> skorCol = new TableColumn<>("Skor SAW");
        skorCol.setCellValueFactory(new PropertyValueFactory<>("skorSAW"));
        skorCol.setPrefWidth(120);
        skorCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            private final DecimalFormat df = new DecimalFormat("#.######");
            
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(df.format(item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #4299e1;");
                }
            }
        });
        
        // Status column
        TableColumn<Penilaian, Integer> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("ranking"));
        statusCol.setPrefWidth(150);
        statusCol.setCellFactory(column -> new TableCell<Penilaian, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
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
        
        rankingTable.getColumns().addAll(rankCol, namaCol, kelasCol, raportCol, akhlakCol, ekskulCol, absensiCol, skorCol, statusCol);
        rankingData = FXCollections.observableArrayList();
        rankingTable.setItems(rankingData);
        
        // Enhanced table styling to match dashboard
        rankingTable.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-table-cell-border-color: #e2e8f0;"
        );
    }
    
    private void loadData() {
        // Load main ranking data
        List<Penilaian> allRankings = sawController.getAllRankings();
        rankingData.clear();
        rankingData.addAll(allRankings);
        
        // Load filter options
        kelasFilter.getItems().clear();
        kelasFilter.getItems().add("Semua Kelas");
        kelasFilter.getItems().addAll(sawController.getAvailableKelas());
        kelasFilter.setValue("Semua Kelas");
        
        // Update stats
        updateStats(allRankings);
        
        // Update top three section
        updateTopThree(allRankings);
    }
    
    private void updateStats(List<Penilaian> rankings) {
        if (rankings.isEmpty()) {
            totalSantriLabel.setText("0");
            avgScoreLabel.setText("0.0000");
            maxScoreLabel.setText("0.0000");
            return;
        }
        
        totalSantriLabel.setText(String.valueOf(rankings.size()));
        
        double avgScore = rankings.stream()
                .mapToDouble(Penilaian::getSkorSAW)
                .average()
                .orElse(0.0);
        
        DecimalFormat df = new DecimalFormat("#.####");
        avgScoreLabel.setText(df.format(avgScore));
        
        // Update max score
        double maxScore = rankings.stream()
                .mapToDouble(Penilaian::getSkorSAW)
                .max()
                .orElse(0.0);
        
        maxScoreLabel.setText(df.format(maxScore));
    }
    
    private void updateTopThree(List<Penilaian> rankings) {
        HBox podiumBox = (HBox) topThreeSection.getChildren().get(1);
        podiumBox.getChildren().clear();
        
        if (rankings.isEmpty()) {
            VBox noDataCard = new VBox(20);
            noDataCard.setPrefWidth(400);
            noDataCard.setPrefHeight(200);
            noDataCard.setPadding(new Insets(40));
            noDataCard.setAlignment(Pos.CENTER);
            noDataCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0.5, 0, 5);" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 20;"
            );
            
            Label noDataIcon = new Label("📊");
            noDataIcon.setFont(Font.font("Arial", 48));
            
            Label noDataLabel = new Label("Belum ada data ranking");
            noDataLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            noDataLabel.setTextFill(Color.web("#4a5568"));
            
            Label instructionLabel = new Label("Silakan lakukan perhitungan SAW terlebih dahulu");
            instructionLabel.setFont(Font.font("Arial", 14));
            instructionLabel.setTextFill(Color.web("#718096"));
            
            noDataCard.getChildren().addAll(noDataIcon, noDataLabel, instructionLabel);
            podiumBox.getChildren().add(noDataCard);
            return;
        }
        
        // Show top 3 (or less if not enough data)
        for (int i = 0; i < Math.min(3, rankings.size()); i++) {
            VBox podiumCard = createPodiumCard(i + 1, rankings.get(i));
            podiumBox.getChildren().add(podiumCard);
        }
    }
    
    private void applyFilter() {
        String selectedKelas = kelasFilter.getValue();
        
        if (selectedKelas == null || selectedKelas.equals("Semua Kelas")) {
            // Show all data
            List<Penilaian> allRankings = sawController.getAllRankings();
            rankingData.clear();
            rankingData.addAll(allRankings);
            updateStats(allRankings);
            updateTopThree(allRankings);
        } else {
            // Filter by class
            List<Penilaian> filteredRankings = sawController.getRankingsByKelas(selectedKelas);
            rankingData.clear();
            rankingData.addAll(filteredRankings);
            updateStats(filteredRankings);
            updateTopThree(filteredRankings);
        }
    }
    
    private void recalculateSAW() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi");
        confirmAlert.setHeaderText("Hitung Ulang SAW");
        confirmAlert.setContentText("Apakah Anda yakin ingin menghitung ulang semua skor SAW? Proses ini akan menimpa hasil perhitungan sebelumnya.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    sawController.recalculateSAW();
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Perhitungan SAW berhasil diperbarui!");
                    loadData(); // Refresh display
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal melakukan perhitungan ulang: " + e.getMessage());
                }
            }
        });
    }
    
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        button.setPrefHeight(35);
        button.setTextFill(Color.WHITE);
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 16 8 16;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: derive(" + color + ", -10%);" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        return button;
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
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
}