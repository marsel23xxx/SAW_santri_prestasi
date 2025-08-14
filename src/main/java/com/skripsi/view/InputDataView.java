// src/main/java/com/skripsi/view/InputDataView.java - UPDATED VERSION
package com.skripsi.view;

import com.skripsi.controller.InputDataController;
import com.skripsi.service.AuthService;
import com.skripsi.model.Santri;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.Optional;

public class InputDataView extends Application {
    
    private AuthService authService;
    private InputDataController inputController;
    private TabPane mainTabPane;
    private TableView<Santri> santriTable;
    private TableView<Penilaian> penilaianTable;
    private ObservableList<Santri> santriData;
    private ObservableList<Penilaian> penilaianData;
    
    // Form fields - UPDATED: Changed kelasField to ComboBox
    private TextField namaField, tahunAjaranField;
    private ComboBox<String> kelasComboBox; // Changed from TextField to ComboBox
    private ComboBox<Santri> santriComboBox;
    private TextField nilaiRaportField, nilaiAkhlakField, nilaiEkstrakurikulerField, nilaiAbsensiField;
    private Label statusLabel;
    
    // Class options
    private final ObservableList<String> kelasOptions = FXCollections.observableArrayList(
        "1 Intensif", "3 Intensif", "1 TMI", 
        "2 TMI", "3 TMI", "4 TMI", "5 TMI",
        "6 TMI"
    );
    
    public InputDataView(AuthService authService) {
        this.authService = authService;
        this.inputController = new InputDataController(authService);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Input Data - Sistem SAW Santri Berprestasi");
        
        createMainLayout();
        setupTables();
        loadData();
        
        Scene scene = new Scene(mainTabPane, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        startAnimation();
    }
    
    private void createMainLayout() {
        mainTabPane = new TabPane();
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Tab 1: Data Santri
        Tab santriTab = new Tab("📚 Data Santri");
        santriTab.setContent(createSantriTabContent());
        
        // Tab 2: Input Penilaian
        Tab penilaianTab = new Tab("📊 Input Penilaian");
        penilaianTab.setContent(createPenilaianTabContent());
        
        mainTabPane.getTabs().addAll(santriTab, penilaianTab);
    }
    
    private VBox createSantriTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f7fafc;");
        
        // Header
        Label headerLabel = new Label("Manajemen Data Santri");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#2d3748"));
        
        // Form section
        VBox formSection = createSantriForm();
        
        // Table section
        VBox tableSection = createSantriTableSection();
        
        content.getChildren().addAll(headerLabel, formSection, tableSection);
        return content;
    }
    
    private VBox createSantriForm() {
        VBox formSection = new VBox(15);
        formSection.setPadding(new Insets(25));
        formSection.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );
        
        Label formTitle = new Label("Tambah Data Santri");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        formTitle.setTextFill(Color.web("#2d3748"));
        
        // Form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER_LEFT);
        
        // Nama field
        Label namaLabel = new Label("Nama Santri:");
        namaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        namaField = new TextField();
        namaField.setPromptText("Masukkan nama lengkap santri");
        styleTextField(namaField);
        
        // UPDATED: Kelas ComboBox instead of TextField
        Label kelasLabel = new Label("Kelas:");
        kelasLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        kelasComboBox = new ComboBox<>();
        kelasComboBox.setItems(kelasOptions);
        kelasComboBox.setPromptText("Pilih kelas santri");
        kelasComboBox.setPrefWidth(250);
        kelasComboBox.setPrefHeight(35);
        styleComboBox(kelasComboBox);
        
        // Tahun ajaran field
        Label tahunLabel = new Label("Tahun Ajaran:");
        tahunLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        tahunAjaranField = new TextField();
        tahunAjaranField.setPromptText("Contoh: 2024/2025");
        styleTextField(tahunAjaranField);
        
        formGrid.add(namaLabel, 0, 0);
        formGrid.add(namaField, 1, 0);
        formGrid.add(kelasLabel, 0, 1);
        formGrid.add(kelasComboBox, 1, 1); // Changed from kelasField to kelasComboBox
        formGrid.add(tahunLabel, 0, 2);
        formGrid.add(tahunAjaranField, 1, 2);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button addButton = createStyledButton("Tambah Santri", "#4299e1");
        Button clearButton = createStyledButton("Bersihkan", "#718096");
        
        addButton.setOnAction(e -> handleAddSantri());
        clearButton.setOnAction(e -> clearSantriForm());
        
        buttonBox.getChildren().addAll(addButton, clearButton);
        
        formSection.getChildren().addAll(formTitle, formGrid, buttonBox);
        return formSection;
    }
    
    private VBox createSantriTableSection() {
        VBox tableSection = new VBox(15);
        tableSection.setPadding(new Insets(25));
        tableSection.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );
        
        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label tableTitle = new Label("Daftar Santri");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tableTitle.setTextFill(Color.web("#2d3748"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshButton = createStyledButton("🔄 Refresh", "#48bb78");
        refreshButton.setOnAction(e -> loadSantriData());
        
        tableHeader.getChildren().addAll(tableTitle, spacer, refreshButton);
        
        // Create table
        santriTable = new TableView<>();
        santriTable.setPrefHeight(300);
        
        tableSection.getChildren().addAll(tableHeader, santriTable);
        return tableSection;
    }
    
    private VBox createPenilaianTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f7fafc;");
        
        // Header
        Label headerLabel = new Label("Input Penilaian Santri");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#2d3748"));
        
        // Form section
        VBox formSection = createPenilaianForm();
        
        // Table section
        VBox tableSection = createPenilaianTableSection();
        
        content.getChildren().addAll(headerLabel, formSection, tableSection);
        return content;
    }
    
    private VBox createPenilaianForm() {
        VBox formSection = new VBox(20);
        formSection.setPadding(new Insets(25));
        formSection.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );
        
        Label formTitle = new Label("Input Nilai Santri");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        formTitle.setTextFill(Color.web("#2d3748"));
        
        // Santri selection
        HBox santriBox = new HBox(15);
        santriBox.setAlignment(Pos.CENTER_LEFT);
        
        Label santriLabel = new Label("Pilih Santri:");
        santriLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        santriLabel.setPrefWidth(120);
        
        santriComboBox = new ComboBox<>();
        santriComboBox.setPrefWidth(300);
        santriComboBox.setPromptText("Pilih santri untuk dinilai");
        santriComboBox.setOnAction(e -> loadExistingPenilaian());
        
        santriBox.getChildren().addAll(santriLabel, santriComboBox);
        
        // Kriteria section
        VBox kriteriaSection = createKriteriaSection();
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button saveButton = createStyledButton("Simpan Penilaian", "#48bb78");
        Button calculateButton = createStyledButton("Hitung SAW", "#ed8936");
        Button clearButton = createStyledButton("Bersihkan", "#718096");
        
        saveButton.setOnAction(e -> handleSavePenilaian());
        calculateButton.setOnAction(e -> handleCalculateSAW());
        clearButton.setOnAction(e -> clearPenilaianForm());
        
        buttonBox.getChildren().addAll(saveButton, calculateButton, clearButton);
        
        // Status label
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setVisible(false);
        
        formSection.getChildren().addAll(formTitle, santriBox, kriteriaSection, buttonBox, statusLabel);
        return formSection;
    }
    
    private VBox createKriteriaSection() {
        VBox kriteriaSection = new VBox(15);
        
        Label kriteriaTitle = new Label("Kriteria Penilaian (Nilai 0-100):");
        kriteriaTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        kriteriaTitle.setTextFill(Color.web("#2d3748"));
        
        GridPane kriteriaGrid = new GridPane();
        kriteriaGrid.setHgap(20);
        kriteriaGrid.setVgap(15);
        
        // Nilai Raport
        VBox raportBox = createKriteriaInput("📚 Nilai Raport", "Bobot: 40%", "#4299e1");
        nilaiRaportField = (TextField) ((VBox) raportBox.getChildren().get(2)).getChildren().get(0);
        
        // Nilai Akhlak
        VBox akhlakBox = createKriteriaInput("🤲 Nilai Akhlak", "Bobot: 30%", "#48bb78");
        nilaiAkhlakField = (TextField) ((VBox) akhlakBox.getChildren().get(2)).getChildren().get(0);
        
        // Nilai Ekstrakurikuler
        VBox ekskulBox = createKriteriaInput("🏃 Ekstrakurikuler", "Bobot: 20%", "#ed8936");
        nilaiEkstrakurikulerField = (TextField) ((VBox) ekskulBox.getChildren().get(2)).getChildren().get(0);
        
        // Nilai Absensi
        VBox absensiBox = createKriteriaInput("📅 Nilai Absensi", "Bobot: 10%", "#9f7aea");
        nilaiAbsensiField = (TextField) ((VBox) absensiBox.getChildren().get(2)).getChildren().get(0);
        
        kriteriaGrid.add(raportBox, 0, 0);
        kriteriaGrid.add(akhlakBox, 1, 0);
        kriteriaGrid.add(ekskulBox, 0, 1);
        kriteriaGrid.add(absensiBox, 1, 1);
        
        kriteriaSection.getChildren().addAll(kriteriaTitle, kriteriaGrid);
        return kriteriaSection;
    }
    
    private VBox createKriteriaInput(String title, String subtitle, String color) {
        VBox box = new VBox(8);
        box.setPrefWidth(200);
        box.setPadding(new Insets(15));
        box.setStyle(
            "-fx-background-color: " + color + "15;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-border-width: 2;"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web(color));
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", 12));
        subtitleLabel.setTextFill(Color.web("#718096"));
        
        VBox fieldBox = new VBox(5);
        TextField field = new TextField();
        field.setPromptText("0-100");
        field.setPrefHeight(35);
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 8;"
        );
        
        fieldBox.getChildren().add(field);
        box.getChildren().addAll(titleLabel, subtitleLabel, fieldBox);
        return box;
    }
    
    private VBox createPenilaianTableSection() {
        VBox tableSection = new VBox(15);
        tableSection.setPadding(new Insets(25));
        tableSection.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );
        
        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label tableTitle = new Label("Data Penilaian");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tableTitle.setTextFill(Color.web("#2d3748"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshButton = createStyledButton("🔄 Refresh", "#48bb78");
        refreshButton.setOnAction(e -> loadPenilaianData());
        
        tableHeader.getChildren().addAll(tableTitle, spacer, refreshButton);
        
        // Create table
        penilaianTable = new TableView<>();
        penilaianTable.setPrefHeight(250);
        
        tableSection.getChildren().addAll(tableHeader, penilaianTable);
        return tableSection;
    }
    
    private void setupTables() {
        setupSantriTable();
        setupPenilaianTable();
    }
    
    @SuppressWarnings("unchecked")
    private void setupSantriTable() {
        TableColumn<Santri, String> namaCol = new TableColumn<>("Nama Santri");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        namaCol.setPrefWidth(200);
        
        TableColumn<Santri, String> kelasCol = new TableColumn<>("Kelas");
        kelasCol.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        kelasCol.setPrefWidth(100);
        
        TableColumn<Santri, String> tahunCol = new TableColumn<>("Tahun Ajaran");
        tahunCol.setCellValueFactory(new PropertyValueFactory<>("tahunAjaran"));
        tahunCol.setPrefWidth(120);
        
        TableColumn<Santri, Void> actionCol = new TableColumn<>("Aksi");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(param -> new TableCell<Santri, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Hapus");
            private final HBox buttons = new HBox(5, editButton, deleteButton);
            
            {
                editButton.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; -fx-background-radius: 4;");
                deleteButton.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; -fx-background-radius: 4;");
                buttons.setAlignment(Pos.CENTER);
                
                editButton.setOnAction(e -> editSantri(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> deleteSantri(getTableView().getItems().get(getIndex())));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        santriTable.getColumns().addAll(namaCol, kelasCol, tahunCol, actionCol);
        santriData = FXCollections.observableArrayList();
        santriTable.setItems(santriData);
    }
    
    @SuppressWarnings("unchecked")
    private void setupPenilaianTable() {
        TableColumn<Penilaian, String> namaCol = new TableColumn<>("Nama Santri");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("namaSantri"));
        namaCol.setPrefWidth(150);
        
        TableColumn<Penilaian, String> kelasCol = new TableColumn<>("Kelas");
        kelasCol.setCellValueFactory(new PropertyValueFactory<>("kelasSantri"));
        kelasCol.setPrefWidth(80);
        
        TableColumn<Penilaian, Double> raportCol = new TableColumn<>("Raport");
        raportCol.setCellValueFactory(new PropertyValueFactory<>("nilaiRaport"));
        raportCol.setPrefWidth(70);
        
        TableColumn<Penilaian, Double> akhlakCol = new TableColumn<>("Akhlak");
        akhlakCol.setCellValueFactory(new PropertyValueFactory<>("nilaiAkhlak"));
        akhlakCol.setPrefWidth(70);
        
        TableColumn<Penilaian, Double> ekskulCol = new TableColumn<>("Ekskul");
        ekskulCol.setCellValueFactory(new PropertyValueFactory<>("nilaiEkstrakurikuler"));
        ekskulCol.setPrefWidth(70);
        
        TableColumn<Penilaian, Double> absensiCol = new TableColumn<>("Absensi");
        absensiCol.setCellValueFactory(new PropertyValueFactory<>("nilaiAbsensi"));
        absensiCol.setPrefWidth(70);
        
        TableColumn<Penilaian, Double> skorCol = new TableColumn<>("Skor SAW");
        skorCol.setCellValueFactory(new PropertyValueFactory<>("skorSAW"));
        skorCol.setPrefWidth(80);
        skorCol.setCellFactory(column -> new TableCell<Penilaian, Double>() {
            private final DecimalFormat df = new DecimalFormat("#.####");
            
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(df.format(item));
                }
            }
        });
        
        TableColumn<Penilaian, Void> actionCol = new TableColumn<>("Aksi");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(param -> new TableCell<Penilaian, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Hapus");
            private final HBox buttons = new HBox(5, editButton, deleteButton);
            
            {
                editButton.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 10;");
                deleteButton.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; -fx-background-radius: 4; -fx-font-size: 10;");
                buttons.setAlignment(Pos.CENTER);
                
                editButton.setOnAction(e -> editPenilaian(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> deletePenilaian(getTableView().getItems().get(getIndex())));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        penilaianTable.getColumns().addAll(namaCol, kelasCol, raportCol, akhlakCol, ekskulCol, absensiCol, skorCol, actionCol);
        penilaianData = FXCollections.observableArrayList();
        penilaianTable.setItems(penilaianData);
    }
    
    private void loadData() {
        loadSantriData();
        loadPenilaianData();
        loadSantriComboBox();
    }
    
    private void loadSantriData() {
        santriData.clear();
        santriData.addAll(inputController.getAllSantri());
    }
    
    private void loadPenilaianData() {
        penilaianData.clear();
        penilaianData.addAll(inputController.getAllPenilaian());
    }
    
    private void loadSantriComboBox() {
        // Store current selection to restore if possible
        Santri currentSelection = santriComboBox.getValue();
        
        santriComboBox.getItems().clear();
        santriComboBox.getItems().addAll(inputController.getAllSantri());
        
        // Try to restore selection if it still exists
        if (currentSelection != null) {
            for (Santri santri : santriComboBox.getItems()) {
                if (santri.getId() == currentSelection.getId()) {
                    santriComboBox.setValue(santri);
                    break;
                }
            }
        }
    }
    
    // UPDATED: Modified to use ComboBox for kelas and added confirmation message
    private void handleAddSantri() {
        String nama = namaField.getText().trim();
        String kelas = kelasComboBox.getValue(); // Changed from kelasField.getText()
        String tahunAjaran = tahunAjaranField.getText().trim();
        
        if (nama.isEmpty() || kelas == null || kelas.trim().isEmpty() || tahunAjaran.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Mohon isi semua field!");
            return;
        }
        
        if (inputController.addSantri(nama, kelas.trim(), tahunAjaran)) {
            // UPDATED: Show message with santri name
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                     "Data santri atas nama \"" + nama + "\" berhasil ditambahkan!");
            clearSantriForm();
            
            // Reload all data including ComboBox
            loadSantriData();
            loadSantriComboBox();
            
            // Automatically switch to penilaian tab for convenience
            mainTabPane.getSelectionModel().select(1);
            
            // Find and select the newly added santri in ComboBox
            for (Santri santri : santriComboBox.getItems()) {
                if (santri.getNama().equals(nama)) {
                    santriComboBox.setValue(santri);
                    break;
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menambahkan data santri!");
        }
    }
    
    // UPDATED: Modified to show message with santri name
    private void handleSavePenilaian() {
        Santri selectedSantri = santriComboBox.getValue();
        if (selectedSantri == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Silakan pilih santri terlebih dahulu!");
            return;
        }
        
        String raport = nilaiRaportField.getText().trim();
        String akhlak = nilaiAkhlakField.getText().trim();
        String ekskul = nilaiEkstrakurikulerField.getText().trim();
        String absensi = nilaiAbsensiField.getText().trim();
        
        // Validate input
        InputDataController.ValidationResult validation = inputController.validatePenilaianInput(raport, akhlak, ekskul, absensi);
        if (!validation.isValid()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Valid", validation.getMessage());
            return;
        }
        
        try {
            double nilaiRaport = Double.parseDouble(raport);
            double nilaiAkhlak = Double.parseDouble(akhlak);
            double nilaiEkstrakurikuler = Double.parseDouble(ekskul);
            double nilaiAbsensi = Double.parseDouble(absensi);
            
            if (inputController.addPenilaian(selectedSantri.getId(), nilaiRaport, nilaiAkhlak, nilaiEkstrakurikuler, nilaiAbsensi)) {
                // UPDATED: Show message with santri name
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                         "Data nilai santri atas nama \"" + selectedSantri.getNama() + "\" berhasil diinput!");
                clearPenilaianForm();
                loadPenilaianData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan penilaian!");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Format angka tidak valid!");
        }
    }
    
    private void handleCalculateSAW() {
        try {
            inputController.calculateSAW();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Perhitungan SAW selesai! Silakan lihat hasil di tabel.");
            loadPenilaianData();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal melakukan perhitungan SAW: " + e.getMessage());
        }
    }
    
    private void loadExistingPenilaian() {
        Santri selectedSantri = santriComboBox.getValue();
        if (selectedSantri != null) {
            Penilaian existing = inputController.getPenilaianBySantriId(selectedSantri.getId());
            if (existing != null) {
                nilaiRaportField.setText(String.valueOf(existing.getNilaiRaport()));
                nilaiAkhlakField.setText(String.valueOf(existing.getNilaiAkhlak()));
                nilaiEkstrakurikulerField.setText(String.valueOf(existing.getNilaiEkstrakurikuler()));
                nilaiAbsensiField.setText(String.valueOf(existing.getNilaiAbsensi()));
            } else {
                clearPenilaianFormFields();
            }
        }
    }
    
    // UPDATED: Modified to use ComboBox for kelas in edit dialog
    private void editSantri(Santri santri) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Data Santri");
        dialog.setHeaderText("Edit data santri: " + santri.getNama());
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField namaEdit = new TextField(santri.getNama());
        ComboBox<String> kelasEdit = new ComboBox<>(); // Changed from TextField to ComboBox
        kelasEdit.setItems(kelasOptions);
        kelasEdit.setValue(santri.getKelas()); // Set current value
        TextField tahunEdit = new TextField(santri.getTahunAjaran());
        
        grid.add(new Label("Nama:"), 0, 0);
        grid.add(namaEdit, 1, 0);
        grid.add(new Label("Kelas:"), 0, 1);
        grid.add(kelasEdit, 1, 1);
        grid.add(new Label("Tahun Ajaran:"), 0, 2);
        grid.add(tahunEdit, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedKelas = kelasEdit.getValue();
            if (selectedKelas == null || selectedKelas.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Mohon pilih kelas!");
                return;
            }
            
            if (inputController.updateSantri(santri.getId(), namaEdit.getText(), selectedKelas, tahunEdit.getText())) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                         "Data santri atas nama \"" + namaEdit.getText() + "\" berhasil diupdate!");
                loadSantriData();
                loadSantriComboBox();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengupdate data santri!");
            }
        }
    }
    
    private void deleteSantri(Santri santri) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus data santri?");
        alert.setContentText("Apakah Anda yakin ingin menghapus data santri: " + santri.getNama() + "?\nData penilaian terkait juga akan terhapus.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (inputController.deleteSantri(santri.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                         "Data santri atas nama \"" + santri.getNama() + "\" berhasil dihapus!");
                loadSantriData();
                loadPenilaianData();
                loadSantriComboBox();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus data santri!");
            }
        }
    }
    
    private void editPenilaian(Penilaian penilaian) {
        // Set the santri in combo box
        for (Santri santri : santriComboBox.getItems()) {
            if (santri.getId() == penilaian.getSantriId()) {
                santriComboBox.setValue(santri);
                break;
            }
        }
        
        // Fill the form with existing values
        nilaiRaportField.setText(String.valueOf(penilaian.getNilaiRaport()));
        nilaiAkhlakField.setText(String.valueOf(penilaian.getNilaiAkhlak()));
        nilaiEkstrakurikulerField.setText(String.valueOf(penilaian.getNilaiEkstrakurikuler()));
        nilaiAbsensiField.setText(String.valueOf(penilaian.getNilaiAbsensi()));
        
        // Switch to penilaian tab
        mainTabPane.getSelectionModel().select(1);
        
        showStatus("Data penilaian dimuat untuk diedit. Silakan ubah nilai dan simpan.", "#4299e1");
    }
    
    private void deletePenilaian(Penilaian penilaian) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus data penilaian?");
        alert.setContentText("Apakah Anda yakin ingin menghapus data penilaian santri: " + penilaian.getNamaSantri() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (inputController.deletePenilaian(penilaian.getSantriId())) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", 
                         "Data penilaian santri atas nama \"" + penilaian.getNamaSantri() + "\" berhasil dihapus!");
                loadPenilaianData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus data penilaian!");
            }
        }
    }
    
    // UPDATED: Modified to clear ComboBox
    private void clearSantriForm() {
        namaField.clear();
        kelasComboBox.setValue(null); // Changed from kelasField.clear()
        tahunAjaranField.clear();
    }
    
    private void clearPenilaianForm() {
        santriComboBox.setValue(null);
        clearPenilaianFormFields();
        hideStatus();
    }
    
    // New method to clear only form fields without touching ComboBox
    private void clearPenilaianFormFields() {
        nilaiRaportField.clear();
        nilaiAkhlakField.clear();
        nilaiEkstrakurikulerField.clear();
        nilaiAbsensiField.clear();
    }
    
    private void styleTextField(TextField field) {
        field.setPrefHeight(35);
        field.setPrefWidth(250);
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 8;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #4299e1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-border-width: 2;" +
                    "-fx-padding: 7;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-border-width: 1;" +
                    "-fx-padding: 8;"
                );
            }
        });
    }
    
    // NEW: Method to style ComboBox
    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-border-width: 1;"
        );
        
        comboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                comboBox.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #4299e1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-border-width: 2;"
                );
            } else {
                comboBox.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-border-width: 1;"
                );
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
    
    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Color.web(color));
        statusLabel.setVisible(true);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), statusLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private void hideStatus() {
        if (statusLabel.isVisible()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), statusLabel);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> statusLabel.setVisible(false));
            fadeOut.play();
        }
    }
    
    private void startAnimation() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), mainTabPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}