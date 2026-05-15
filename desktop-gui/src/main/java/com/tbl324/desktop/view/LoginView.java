package com.tbl324.desktop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.BiConsumer;

public class LoginView extends StackPane {

    private final TextField     usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label         errorLabel    = new Label();

    public LoginView(BiConsumer<String, String> onLogin) {
        buildUi(onLogin);
    }

    private void buildUi(BiConsumer<String, String> onLogin) {
        setStyle("-fx-background-color: #F5F5F5;");

        // ── Card ──────────────────────────────────────────────────────────
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setMaxWidth(400);
        card.setSpacing(0);

        // Blue header band
        VBox cardHeader = new VBox(4);
        cardHeader.setAlignment(Pos.CENTER);
        cardHeader.setPadding(new Insets(0, 0, 24, 0));

        Label appLabel = new Label("TBL324");
        appLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        appLabel.setStyle("-fx-text-fill: #1565C0;");

        Label subLabel = new Label("Event Ticketing");
        subLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subLabel.setStyle("-fx-text-fill: #757575;");

        cardHeader.getChildren().addAll(appLabel, subLabel);

        // Form
        VBox form = new VBox(8);
        form.setPadding(new Insets(0, 0, 8, 0));

        Label userLabel = new Label("Kullanıcı Adı");
        userLabel.setStyle("-fx-text-fill: #424242; -fx-font-weight: bold; -fx-font-size: 12px;");
        usernameField.setPromptText("Kullanıcı adınızı girin");
        usernameField.setPrefHeight(40);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        Label passLabel = new Label("Şifre");
        passLabel.setStyle("-fx-text-fill: #424242; -fx-font-weight: bold; -fx-font-size: 12px;");
        passwordField.setPromptText("Şifrenizi girin");
        passwordField.setPrefHeight(40);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        VBox userBox = new VBox(4, userLabel, usernameField);
        VBox passBox = new VBox(4, passLabel, passwordField);
        passBox.setPadding(new Insets(8, 0, 0, 0));

        form.getChildren().addAll(userBox, passBox);

        // Error
        errorLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(errorLabel, new Insets(8, 0, 0, 0));

        // Login button
        Button loginBtn = new Button("Giriş Yap");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setDefaultButton(true);
        VBox.setMargin(loginBtn, new Insets(16, 0, 0, 0));

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Kullanıcı adı ve şifre gerekli.");
                return;
            }
            errorLabel.setText("");
            loginBtn.setDisable(true);
            loginBtn.setText("Giriş yapılıyor...");
            onLogin.accept(user, pass);
        });

        passwordField.setOnAction(e -> loginBtn.fire());

        card.getChildren().addAll(cardHeader, form, errorLabel, loginBtn);

        setAlignment(Pos.CENTER);
        getChildren().add(card);
    }

    public void showError(String msg) {
        errorLabel.setText(msg);
        // Re-enable button on error
        getChildren().stream()
                .filter(n -> n instanceof VBox)
                .map(n -> (VBox) n)
                .flatMap(v -> v.getChildren().stream())
                .filter(n -> n instanceof Button)
                .map(n -> (Button) n)
                .findFirst()
                .ifPresent(btn -> {
                    btn.setDisable(false);
                    btn.setText("Giriş Yap");
                });
    }
}
