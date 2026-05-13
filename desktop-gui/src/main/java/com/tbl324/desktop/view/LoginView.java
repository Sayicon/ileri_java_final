package com.tbl324.desktop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.BiConsumer;

public class LoginView extends VBox {

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label errorLabel = new Label();

    public LoginView(BiConsumer<String, String> onLogin) {
        buildUi(onLogin);
    }

    private void buildUi(BiConsumer<String, String> onLogin) {
        Label title = new Label("TBL324 Event Ticketing");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Kullanıcı Adı:"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Şifre:"), 0, 1);
        form.add(passwordField, 1, 1);

        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginBtn = new Button("Giriş Yap");
        loginBtn.setDefaultButton(true);
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Kullanıcı adı ve şifre gerekli.");
                return;
            }
            errorLabel.setText("");
            onLogin.accept(user, pass);
        });

        setAlignment(Pos.CENTER);
        setSpacing(16);
        setPadding(new Insets(40));
        getChildren().addAll(title, form, errorLabel, loginBtn);
    }

    public void showError(String msg) {
        errorLabel.setText(msg);
    }
}
