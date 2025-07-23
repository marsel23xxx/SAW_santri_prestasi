// src/main/java/com/skripsi/view/LoginForm.java (Updated)
package com.skripsi.view;

import com.skripsi.controller.LoginController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Node;

public class LoginForm extends Application {
    
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton, registerButton;
    private VBox mainContainer;
    private Label statusLabel;
    private LoginController loginController;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistem SAW Santri Berprestasi");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        
        // Initialize controller
        loginController = new LoginController();
        
        mainContainer = createMainContainer();
        VBox loginCard = createLoginCard();
        mainContainer.getChildren().add(loginCard);
        
        Scene scene = new Scene(mainContainer, 450, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        startEntranceAnimation();
        makeWindowDraggable(scene, primaryStage);
        
        // Add close button
        addCloseButton(primaryStage);
    }
    
    private void addCloseButton(Stage stage) {
        Button closeButton = new Button("✕");
        closeButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> stage.close());
        
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));
        topBar.getChildren().add(closeButton);
        
        mainContainer.getChildren().add(0, topBar);
    }
    
    private VBox createMainContainer() {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null,
            new Stop(0, Color.web("#667eea")),
            new Stop(1, Color.web("#764ba2"))
        );
        
        BackgroundFill bgFill = new BackgroundFill(gradient, null, null);
        Background bg = new Background(bgFill);
        container.setBackground(bg);
        
        return container;
    }
    
    private VBox createLoginCard() {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(350);
        card.setMaxHeight(500);
        
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 10);"
        );
        
        VBox header = createHeader();
        VBox form = createForm();
        VBox buttons = createButtons();
        
        card.getChildren().addAll(header, form, buttons);
        return card;
    }
    
    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER);
        
        Label icon = new Label("🏫 SAW SANTRI");
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        icon.setTextFill(Color.web("#667eea"));
        
        Label title = new Label("Sistem Penilaian");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#2d3748"));
        
        Label subtitle = new Label("Santri Berprestasi");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setTextFill(Color.web("#718096"));
        
        header.getChildren().addAll(icon, title, subtitle);
        return header;
    }
    
    private VBox createForm() {
        VBox form = new VBox(20);
        form.setAlignment(Pos.CENTER);
        
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        usernameLabel.setTextFill(Color.web("#2d3748"));
        
        usernameField = new TextField();
        usernameField.setPromptText("Masukkan username");
        usernameField.setText("admin"); // Default for testing
        styleTextField(usernameField);
        
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        passwordLabel.setTextFill(Color.web("#2d3748"));
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password");
        passwordField.setText("password"); // Default for testing
        styleTextField(passwordField);
        
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        HBox options = new HBox();
        options.setAlignment(Pos.CENTER_LEFT);
        options.setSpacing(10);
        
        CheckBox rememberMe = new CheckBox("Ingat saya");
        rememberMe.setFont(Font.font("Arial", 13));
        rememberMe.setTextFill(Color.web("#718096"));
        
        Label info = new Label("Demo: admin/password");
        info.setFont(Font.font("Arial", 12));
        info.setTextFill(Color.web("#a0aec0"));
        
        HBox.setHgrow(rememberMe, Priority.ALWAYS);
        options.getChildren().addAll(rememberMe, info);
        
        form.getChildren().addAll(usernameBox, passwordBox, options);
        return form;
    }
    
    private void styleTextField(TextInputControl field) {
        field.setPrefHeight(48);
        field.setFont(Font.font("Arial", 15));
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 12 16 12 16;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #667eea;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-width: 2;" +
                    "-fx-padding: 11 15 11 15;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-width: 1;" +
                    "-fx-padding: 12 16 12 16;"
                );
            }
        });
        
        // Add Enter key support
        field.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                handleLogin();
            }
        });
    }
    
    private VBox createButtons() {
        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);
        
        loginButton = new Button("Masuk Sistem");
        styleButton(loginButton, "#667eea", "#5a6fd8");
        loginButton.setOnAction(e -> handleLogin());
        
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setVisible(false);
        
        buttons.getChildren().addAll(loginButton, statusLabel);
        return buttons;
    }
    
    private void styleButton(Button button, String color, String hoverColor) {
        button.setPrefWidth(300);
        button.setPrefHeight(48);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        button.setTextFill(Color.WHITE);
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Mohon isi semua field!", Color.web("#f56565"));
            shakeAnimation(mainContainer);
            return;
        }
        
        // Use controller for authentication
        if (loginController.handleLogin(username, password)) {
            showStatus("✓ Login berhasil!", Color.web("#48bb78"));
            successAnimation();
            
            Timeline openDashboard = new Timeline(
                new KeyFrame(Duration.seconds(1.5), e -> {
                    loginController.openDashboard((Stage) loginButton.getScene().getWindow());
                })
            );
            openDashboard.play();
            
        } else {
            showStatus("Username atau password salah!", Color.web("#f56565"));
            shakeAnimation(mainContainer);
            passwordField.clear();
        }
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
        statusLabel.setVisible(true);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), statusLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        Timeline hideTimer = new Timeline(
            new KeyFrame(Duration.seconds(3), e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), statusLabel);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(evt -> statusLabel.setVisible(false));
                fadeOut.play();
            })
        );
        hideTimer.play();
    }
    
    private void startEntranceAnimation() {
        mainContainer.setOpacity(0);
        mainContainer.setTranslateY(50);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(800), mainContainer);
        slideUp.setFromY(50);
        slideUp.setToY(0);
        
        ParallelTransition entrance = new ParallelTransition(fadeIn, slideUp);
        entrance.setInterpolator(Interpolator.EASE_OUT);
        entrance.play();
    }
    
    private void shakeAnimation(Node node) {
        Timeline shake = new Timeline();
        shake.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(50), new KeyValue(node.translateXProperty(), -5)),
            new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), 5)),
            new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), -5)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), 5)),
            new KeyFrame(Duration.millis(250), new KeyValue(node.translateXProperty(), 0))
        );
        shake.play();
    }
    
    private void successAnimation() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), loginButton);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }
    
    private void makeWindowDraggable(Scene scene, Stage stage) {
        final double[] xOffset = {0};
        final double[] yOffset = {0};
        
        scene.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}