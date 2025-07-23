// src/main/java/com/skripsi/view/DashboardView.java - WINDOW CONSISTENCY FIXED
package com.skripsi.view;

import com.skripsi.controller.DashboardController;
import com.skripsi.service.AuthService;
import com.skripsi.model.Penilaian;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.List;

public class DashboardView extends Application {
    
    private AuthService authService;
    private DashboardController dashboardController;
    private BorderPane mainLayout;
    private VBox sideMenu;
    private StackPane contentArea;
    private Label welcomeLabel;
    private VBox statsContainer;
    
    // Store reference to main stage for consistent window management
    private Stage primaryStage;
    
    public DashboardView(AuthService authService) {
        this.authService = authService;
        this.dashboardController = new DashboardController(authService);
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Store reference for child windows
        
        primaryStage.setTitle("Dashboard - Sistem SAW Santri Berprestasi");
        
        createMainLayout();
        loadDashboardContent();
        
        Scene scene = new Scene(mainLayout, 1200, 800);
        // Add global CSS to ensure proper rendering
        scene.getStylesheets().add("data:text/css," + 
            ".root { -fx-font-family: 'Arial', sans-serif; }" +
            ".stats-card { -fx-background-color: white; -fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3); " +
            "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 15; }"
        );
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        startEntranceAnimation();
    }
    
    private void createMainLayout() {
        mainLayout = new BorderPane();
        
        // Create components
        createSideMenu();
        createTopBar();
        createContentArea();
        
        // Set layout
        mainLayout.setLeft(sideMenu);
        mainLayout.setCenter(contentArea);
    }
    
    private void createSideMenu() {
        sideMenu = new VBox(10);
        sideMenu.setPrefWidth(280);
        sideMenu.setMinWidth(280);
        sideMenu.setMaxWidth(280);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setAlignment(Pos.TOP_CENTER);
        
        // Gradient background
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, null,
            new Stop(0, Color.web("#667eea")),
            new Stop(1, Color.web("#764ba2"))
        );
        BackgroundFill bgFill = new BackgroundFill(gradient, new CornerRadii(0), Insets.EMPTY);
        sideMenu.setBackground(new Background(bgFill));
        
        // Logo/Title
        VBox logoSection = createLogoSection();
        
        // Menu items
        VBox menuItems = createMenuItems();
        
        // User info
        VBox userSection = createUserSection();
        
        sideMenu.getChildren().addAll(logoSection, menuItems, userSection);
        VBox.setVgrow(menuItems, Priority.ALWAYS);
    }
    
    private VBox createLogoSection() {
        VBox logoSection = new VBox(10);
        logoSection.setAlignment(Pos.CENTER);
        logoSection.setPadding(new Insets(0, 0, 30, 0));
        
        Label logo = new Label("🏫");
        logo.setFont(Font.font("Arial", 40));
        
        Label title = new Label("SAW Santri");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        
        Label subtitle = new Label("Sistem Penilaian");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#e2e8f0"));
        
        logoSection.getChildren().addAll(logo, title, subtitle);
        return logoSection;
    }
    
    private VBox createMenuItems() {
        VBox menuItems = new VBox(5);
        menuItems.setAlignment(Pos.TOP_CENTER);
        
        Button dashboardBtn = createMenuButton("📊", "Dashboard", true);
        Button inputDataBtn = createMenuButton("📝", "Input Data", false);
        Button hasilSAWBtn = createMenuButton("🏆", "Hasil SAW", false);
        Button laporanBtn = createMenuButton("📋", "Laporan", false);
        
        dashboardBtn.setOnAction(e -> loadDashboardContent());
        inputDataBtn.setOnAction(e -> openInputDataView());
        hasilSAWBtn.setOnAction(e -> openSAWResultView());
        laporanBtn.setOnAction(e -> openReportView()); // FIXED: Now opens ReportView
        
        menuItems.getChildren().addAll(dashboardBtn, inputDataBtn, hasilSAWBtn, laporanBtn);
        return menuItems;
    }
    
    private Button createMenuButton(String icon, String text, boolean active) {
        Button button = new Button();
        
        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(0, 0, 0, 10));
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 18));
        iconLabel.setTextFill(Color.WHITE);
        
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        textLabel.setTextFill(Color.WHITE);
        
        content.getChildren().addAll(iconLabel, textLabel);
        button.setGraphic(content);
        button.setText("");
        
        button.setPrefWidth(240);
        button.setPrefHeight(50);
        
        if (active) {
            button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.2);" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;"
            );
        } else {
            button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;"
            );
        }
        
        button.setOnMouseEntered(e -> {
            if (!active) {
                button.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;"
                );
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!active) {
                button.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;"
                );
            }
        });
        
        return button;
    }
    
    private VBox createUserSection() {
        VBox userSection = new VBox(10);
        userSection.setAlignment(Pos.CENTER);
        userSection.setPadding(new Insets(20, 0, 0, 0));
        
        // User avatar
        Label avatar = new Label("👤");
        avatar.setFont(Font.font("Arial", 30));
        
        // User name
        Label userName = new Label(authService.getCurrentUser().getNamaLengkap());
        userName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        userName.setTextFill(Color.WHITE);
        
        // User role
        Label userRole = new Label(authService.getCurrentUser().getRole().toUpperCase());
        userRole.setFont(Font.font("Arial", 12));
        userRole.setTextFill(Color.web("#e2e8f0"));
        
        // Logout button
        Button logoutBtn = new Button("Keluar");
        logoutBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        logoutBtn.setPrefWidth(120);
        logoutBtn.setPrefHeight(35);
        logoutBtn.setTextFill(Color.WHITE);
        logoutBtn.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2);" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );
        
        logoutBtn.setOnAction(e -> handleLogout());
        
        userSection.getChildren().addAll(avatar, userName, userRole, logoutBtn);
        return userSection;
    }
    
    private void createTopBar() {
        HBox topBar = new HBox();
        topBar.setPrefHeight(70);
        topBar.setMinHeight(70);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, null,
            new Stop(0, Color.web("#f7fafc")),
            new Stop(1, Color.web("#edf2f7"))
        );
        BackgroundFill bgFill = new BackgroundFill(gradient, new CornerRadii(0), Insets.EMPTY);
        topBar.setBackground(new Background(bgFill));
        
        welcomeLabel = new Label("Dashboard - Selamat Datang!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.web("#2d3748"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label timeLabel = new Label(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        ));
        timeLabel.setFont(Font.font("Arial", 14));
        timeLabel.setTextFill(Color.web("#718096"));
        
        topBar.getChildren().addAll(welcomeLabel, spacer, timeLabel);
        mainLayout.setTop(topBar);
    }
    
    private void createContentArea() {
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));
        contentArea.setStyle("-fx-background-color: #f7fafc;");
    }
    
    private void loadDashboardContent() {
        welcomeLabel.setText("Dashboard - Selamat Datang!");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox dashboardContent = new VBox(30);
        dashboardContent.setPadding(new Insets(0));
        dashboardContent.setAlignment(Pos.TOP_CENTER);
        dashboardContent.setFillWidth(true);
        
        // Stats cards
        HBox statsCards = createStatsCards();
        
        // Charts section
        HBox chartsSection = createChartsSection();
        
        // Top santri section
        VBox topSantriSection = createTopSantriSection();
        
        dashboardContent.getChildren().addAll(statsCards, chartsSection, topSantriSection);
        scrollPane.setContent(dashboardContent);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(scrollPane);
    }
    
    private HBox createStatsCards() {
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);
        statsCards.setPadding(new Insets(10));
        
        // Force proper sizing
        statsCards.setMinHeight(150);
        statsCards.setPrefHeight(150);
        
        DashboardController.DashboardStats stats = dashboardController.getDashboardStats();
        
        VBox santriCard = createStatsCard("👥", "Total Santri", 
            String.valueOf(stats.getTotalSantri()), "#4299e1");
        VBox penilaianCard = createStatsCard("📊", "Data Penilaian", 
            String.valueOf(stats.getTotalPenilaian()), "#48bb78");
        VBox kelasCard = createStatsCard("🏫", "Total Kelas", 
            String.valueOf(stats.getTotalKelas()), "#ed8936");
        VBox rankingCard = createStatsCard("🏆", "Sudah Dihitung", 
            stats.getTotalPenilaian() > 0 ? "Ya" : "Belum", "#9f7aea");
        
        // Ensure equal distribution
        HBox.setHgrow(santriCard, Priority.ALWAYS);
        HBox.setHgrow(penilaianCard, Priority.ALWAYS);
        HBox.setHgrow(kelasCard, Priority.ALWAYS);
        HBox.setHgrow(rankingCard, Priority.ALWAYS);
        
        statsCards.getChildren().addAll(santriCard, penilaianCard, kelasCard, rankingCard);
        return statsCards;
    }
    
    private VBox createStatsCard(String icon, String title, String value, String color) {
        VBox card = new VBox(15);
        
        // Fixed sizing
        card.setPrefWidth(220);
        card.setMinWidth(220);
        card.setMaxWidth(220);
        card.setPrefHeight(130);
        card.setMinHeight(130);
        card.setMaxHeight(130);
        
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        
        // Enhanced styling with better visibility
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        // Add CSS class for consistent styling
        card.getStyleClass().add("stats-card");
        
        // Force visible text with explicit styling
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 28));
        iconLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.setStyle("-fx-text-fill: #2d3748; -fx-font-weight: normal;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: #1a202c; -fx-font-weight: bold;");
        valueLabel.setWrapText(true);
        valueLabel.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        
        // Enhanced hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.6, 0, 5);" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        return card;
    }
    
    private HBox createChartsSection() {
        HBox chartsSection = new HBox(20);
        chartsSection.setAlignment(Pos.CENTER);
        chartsSection.setPadding(new Insets(10));
        
        // Kriteria weights chart
        VBox criteriaChart = createCriteriaWeightsCard();
        
        // Quick actions card
        VBox actionsCard = createQuickActionsCard();
        
        // Ensure equal distribution
        HBox.setHgrow(criteriaChart, Priority.ALWAYS);
        HBox.setHgrow(actionsCard, Priority.ALWAYS);
        
        chartsSection.getChildren().addAll(criteriaChart, actionsCard);
        return chartsSection;
    }
    
    private VBox createCriteriaWeightsCard() {
        VBox card = new VBox(20);
        card.setPrefWidth(450);
        card.setMinWidth(450);
        card.setPrefHeight(320);
        card.setMinHeight(320);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        Label title = new Label("Bobot Kriteria SAW");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2d3748"));
        
        VBox criteria = new VBox(15);
        criteria.getChildren().addAll(
            createCriteriaBar("Nilai Raport", 0.4, "#4299e1"),
            createCriteriaBar("Nilai Akhlak", 0.3, "#48bb78"),
            createCriteriaBar("Ekstrakurikuler", 0.2, "#ed8936"),
            createCriteriaBar("Absensi", 0.1, "#9f7aea")
        );
        
        card.getChildren().addAll(title, criteria);
        return card;
    }
    
    private HBox createCriteriaBar(String label, double weight, String color) {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Arial", 14));
        nameLabel.setPrefWidth(130);
        nameLabel.setTextFill(Color.web("#4a5568"));
        
        ProgressBar progressBar = new ProgressBar(weight);
        progressBar.setPrefWidth(150);
        progressBar.setPrefHeight(12);
        progressBar.setStyle(
            "-fx-accent: " + color + ";" +
            "-fx-background-color: #e2e8f0;" +
            "-fx-background-radius: 6;" +
            "-fx-background-insets: 0;"
        );
        
        Label percentLabel = new Label(String.format("%.0f%%", weight * 100));
        percentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        percentLabel.setTextFill(Color.web(color));
        percentLabel.setPrefWidth(40);
        
        bar.getChildren().addAll(nameLabel, progressBar, percentLabel);
        return bar;
    }
    
    private VBox createQuickActionsCard() {
        VBox card = new VBox(20);
        card.setPrefWidth(450);
        card.setMinWidth(450);
        card.setPrefHeight(320);
        card.setMinHeight(320);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        Label title = new Label("Aksi Cepat");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2d3748"));
        
        VBox actions = new VBox(15);
        
        Button inputDataBtn = createActionButton("📝", "Input Data Santri", "#4299e1");
        Button hitungSAWBtn = createActionButton("🔢", "Hitung SAW", "#48bb78");
        Button lihatRankingBtn = createActionButton("🏆", "Lihat Ranking", "#ed8936");
        
        inputDataBtn.setOnAction(e -> openInputDataView());
        hitungSAWBtn.setOnAction(e -> calculateSAW());
        lihatRankingBtn.setOnAction(e -> openSAWResultView());
        
        actions.getChildren().addAll(inputDataBtn, hitungSAWBtn, lihatRankingBtn);
        card.getChildren().addAll(title, actions);
        return card;
    }
    
    private Button createActionButton(String icon, String text, String color) {
        Button button = new Button();
        
        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(0, 0, 0, 15));
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 18));
        iconLabel.setTextFill(Color.WHITE);
        
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        textLabel.setTextFill(Color.WHITE);
        
        content.getChildren().addAll(iconLabel, textLabel);
        button.setGraphic(content);
        button.setText("");
        
        button.setPrefWidth(380);
        button.setPrefHeight(50);
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: derive(" + color + ", -15%);" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        return button;
    }
    
    private VBox createTopSantriSection() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(10));
        
        Label titleLabel = new Label("🏆 Top 5 Santri Berprestasi");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#2d3748"));
        
        VBox topSantriCard = new VBox(15);
        topSantriCard.setPadding(new Insets(25));
        topSantriCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.5, 0, 3);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;"
        );
        
        DashboardController.DashboardStats stats = dashboardController.getDashboardStats();
        List<Penilaian> topSantri = stats.getTopSantri();
        
        if (topSantri.isEmpty()) {
            Label noDataLabel = new Label("Belum ada data penilaian. Silakan input data terlebih dahulu.");
            noDataLabel.setFont(Font.font("Arial", 16));
            noDataLabel.setTextFill(Color.web("#718096"));
            noDataLabel.setAlignment(Pos.CENTER);
            noDataLabel.setPadding(new Insets(30));
            topSantriCard.getChildren().add(noDataLabel);
        } else {
            for (int i = 0; i < Math.min(5, topSantri.size()); i++) {
                Penilaian p = topSantri.get(i);
                HBox santriRow = createTopSantriRow(i + 1, p);
                topSantriCard.getChildren().add(santriRow);
            }
        }
        
        section.getChildren().addAll(titleLabel, topSantriCard);
        return section;
    }
    
    private HBox createTopSantriRow(int rank, Penilaian penilaian) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle(
            "-fx-background-color: #f7fafc;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );
        
        // Rank badge
        Label rankLabel = new Label(String.valueOf(rank));
        rankLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        rankLabel.setTextFill(Color.WHITE);
        rankLabel.setPrefWidth(35);
        rankLabel.setPrefHeight(35);
        rankLabel.setAlignment(Pos.CENTER);
        
        String badgeColor = rank == 1 ? "#ffd700" : rank == 2 ? "#c0c0c0" : rank == 3 ? "#cd7f32" : "#718096";
        rankLabel.setStyle(
            "-fx-background-color: " + badgeColor + ";" +
            "-fx-background-radius: 18;"
        );
        
        // Santri info
        VBox santriInfo = new VBox(5);
        
        Label namaLabel = new Label(penilaian.getNamaSantri());
        namaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        namaLabel.setTextFill(Color.web("#2d3748"));
        
        Label kelasLabel = new Label("Kelas: " + penilaian.getKelasSantri());
        kelasLabel.setFont(Font.font("Arial", 14));
        kelasLabel.setTextFill(Color.web("#718096"));
        
        santriInfo.getChildren().addAll(namaLabel, kelasLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Score
        DecimalFormat df = new DecimalFormat("#.####");
        Label scoreLabel = new Label("Skor: " + df.format(penilaian.getSkorSAW()));
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        scoreLabel.setTextFill(Color.web("#4299e1"));
        
        row.getChildren().addAll(rankLabel, santriInfo, spacer, scoreLabel);
        
        // Hover effect
        row.setOnMouseEntered(e -> {
            row.setStyle(
                "-fx-background-color: #edf2f7;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #4299e1;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;"
            );
        });
        
        row.setOnMouseExited(e -> {
            row.setStyle(
                "-fx-background-color: #f7fafc;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
            );
        });
        
        return row;
    }
    
    // FIXED: Pass stage configuration to child windows for consistency
    private void openInputDataView() {
        try {
            InputDataView inputDataView = new InputDataView(authService);
            Stage inputStage = new Stage();
            
            // FIXED: Set same owner and initial size as dashboard
            inputStage.initOwner(primaryStage);
            inputStage.setWidth(primaryStage.getWidth());
            inputStage.setHeight(primaryStage.getHeight());
            inputStage.setX(primaryStage.getX());
            inputStage.setY(primaryStage.getY());
            
            inputDataView.start(inputStage);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal membuka halaman input data: " + e.getMessage());
        }
    }
    
    private void openSAWResultView() {
        try {
            SAWResultView sawResultView = new SAWResultView();
            Stage resultStage = new Stage();
            
            // FIXED: Set same owner and initial size as dashboard
            resultStage.initOwner(primaryStage);
            resultStage.setWidth(primaryStage.getWidth());
            resultStage.setHeight(primaryStage.getHeight());
            resultStage.setX(primaryStage.getX());
            resultStage.setY(primaryStage.getY());
            
            sawResultView.start(resultStage);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal membuka halaman hasil SAW: " + e.getMessage());
        }
    }
    
    // ADDED: Method to open ReportView
    private void openReportView() {
        try {
            ReportView reportView = new ReportView();
            Stage reportStage = new Stage();
            
            // Set same owner and initial size as dashboard
            reportStage.initOwner(primaryStage);
            reportStage.setWidth(primaryStage.getWidth());
            reportStage.setHeight(primaryStage.getHeight());
            reportStage.setX(primaryStage.getX());
            reportStage.setY(primaryStage.getY());
            
            reportView.start(reportStage);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal membuka halaman laporan: " + e.getMessage());
        }
    }
    
    private void calculateSAW() {
        try {
            // Use SAW calculation service through controller
            dashboardController.getDashboardStats(); // This will trigger any needed calculations
            showInfo("Perhitungan SAW telah selesai. Silakan lihat hasil di menu 'Hasil SAW'.");
            
            // Refresh dashboard to show updated data
            loadDashboardContent();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal melakukan perhitungan SAW: " + e.getMessage());
        }
    }
    
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi");
        alert.setHeaderText("Keluar dari sistem?");
        alert.setContentText("Apakah Anda yakin ingin keluar dari sistem?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                authService.logout();
                try {
                    LoginForm loginForm = new LoginForm();
                    Stage loginStage = new Stage();
                    loginForm.start(loginStage);
                    
                    // Close dashboard
                    primaryStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Terjadi kesalahan");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void startEntranceAnimation() {
        // Fade in animation for the main content
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), contentArea);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        // Slide in animation for side menu
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), sideMenu);
        slideIn.setFromX(-sideMenu.getPrefWidth());
        slideIn.setToX(0);
        
        ParallelTransition entrance = new ParallelTransition(fadeIn, slideIn);
        entrance.play();
    }
}