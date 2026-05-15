package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
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
    private Button              loginBtn;

    private final ApiClient apiClient;

    public LoginView(ApiClient apiClient, BiConsumer<String, String> onLogin) {
        this.apiClient = apiClient;
        buildUi(onLogin);
    }

    private void buildUi(BiConsumer<String, String> onLogin) {
        setStyle("-fx-background-color: #F5F5F5;");

        // ── Card ──────────────────────────────────────────────────────────
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setMaxWidth(400);
        card.setSpacing(0);

        // Header
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
        loginBtn = new Button("Giriş Yap");
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

        // Register link
        Button registerLink = new Button("Hesabınız yok mu? Kayıt Olun");
        registerLink.setStyle("-fx-background-color: transparent; -fx-text-fill: #1565C0; "
                + "-fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true;");
        registerLink.setMaxWidth(Double.MAX_VALUE);
        registerLink.setAlignment(Pos.CENTER);
        VBox.setMargin(registerLink, new Insets(8, 0, 0, 0));
        registerLink.setOnAction(e -> showRegisterDialog());

        card.getChildren().addAll(cardHeader, form, errorLabel, loginBtn, registerLink);

        setAlignment(Pos.CENTER);
        getChildren().add(card);
    }

    private void showRegisterDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Kayıt Ol");
        dialog.setHeaderText("Yeni hesap oluşturun");

        ButtonType kayitBtn = new ButtonType("Kayıt Ol", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(kayitBtn, ButtonType.CANCEL);

        TextField regUsername = new TextField();
        regUsername.setPromptText("Kullanıcı adı");
        regUsername.setPrefHeight(36);

        TextField regEmail = new TextField();
        regEmail.setPromptText("E-posta");
        regEmail.setPrefHeight(36);

        PasswordField regPassword = new PasswordField();
        regPassword.setPromptText("Şifre");
        regPassword.setPrefHeight(36);

        PasswordField regPasswordConfirm = new PasswordField();
        regPasswordConfirm.setPromptText("Şifre (tekrar)");
        regPasswordConfirm.setPrefHeight(36);

        Label regError = new Label();
        regError.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12px;");
        regError.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.setMinWidth(360);
        grid.add(new Label("Kullanıcı Adı:"), 0, 0); grid.add(regUsername,        1, 0);
        grid.add(new Label("E-posta:"),       0, 1); grid.add(regEmail,           1, 1);
        grid.add(new Label("Şifre:"),         0, 2); grid.add(regPassword,        1, 2);
        grid.add(new Label("Şifre (tekrar):"),0, 3); grid.add(regPasswordConfirm, 1, 3);
        grid.add(regError,                    0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);
        javafx.application.Platform.runLater(regUsername::requestFocus);

        // Kayıt Ol butonunu tıklama davranışını override et — hata varsa kapat
        javafx.scene.Node okButton = dialog.getDialogPane().lookupButton(kayitBtn);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String u = regUsername.getText().trim();
            String em = regEmail.getText().trim();
            String p = regPassword.getText();
            String p2 = regPasswordConfirm.getText();

            if (u.isEmpty() || em.isEmpty() || p.isEmpty()) {
                regError.setText("Tüm alanlar zorunludur.");
                event.consume();
                return;
            }
            if (!p.equals(p2)) {
                regError.setText("Şifreler eşleşmiyor.");
                event.consume();
                return;
            }

            try {
                apiClient.register(u, em, p);
                // Başarılı — kullanıcı adını login formuna doldur
                usernameField.setText(u);
                passwordField.clear();
                passwordField.requestFocus();
            } catch (Exception ex) {
                regError.setText("Kayıt başarısız: " + ex.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    public void showError(String msg) {
        errorLabel.setText(msg);
        loginBtn.setDisable(false);
        loginBtn.setText("Giriş Yap");
    }
}
