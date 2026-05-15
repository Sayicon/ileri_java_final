package com.tbl324.desktop;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.client.ApiException;
import com.tbl324.desktop.view.AdminDashboardView;
import com.tbl324.desktop.view.EventListView;
import com.tbl324.desktop.view.LoginView;
import com.tbl324.desktop.view.MyTicketsView;
import com.tbl324.desktop.view.SeatMapView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Objects;

public class DesktopApp extends Application {

    private static final String GATEWAY_URL = System.getProperty(
            "gateway.url", "http://localhost:8080");

    private ApiClient apiClient;
    private Stage     primaryStage;
    private Long      currentUserId;
    private String    currentUsername;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.apiClient    = new ApiClient(GATEWAY_URL);

        stage.setTitle("TBL324 Event Ticketing");
        stage.setWidth(900);
        stage.setHeight(650);
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        showLogin();
        stage.show();
    }

    private Scene makeScene(Parent root) {
        Scene scene = new Scene(root, 900, 650);
        String css = Objects.requireNonNull(
                getClass().getResource("/com/tbl324/desktop/style.css"),
                "style.css not found").toExternalForm();
        scene.getStylesheets().add(css);
        return scene;
    }

    private void showLogin() {
        LoginView[] ref = new LoginView[1];
        LoginView login = new LoginView((username, password) -> {
            try {
                apiClient.login(username, password);
                currentUserId   = apiClient.getUserId();
                currentUsername = username;
                if ("ADMIN".equals(apiClient.getRole())) {
                    showAdminDashboard();
                } else {
                    showEventList();
                }
            } catch (ApiException ex) {
                ref[0].showError("Giriş başarısız: kullanıcı adı veya şifre hatalı.");
            } catch (Exception ex) {
                ref[0].showError("Bağlantı hatası: " + ex.getMessage());
            }
        });
        ref[0] = login;
        primaryStage.setScene(makeScene(login));
    }

    private void showEventList() {
        EventListView eventList = new EventListView(
                apiClient, currentUserId, currentUsername,
                (event, userId) -> showSeatMap(event.id(), event.title(), userId),
                this::showMyTickets);
        primaryStage.setScene(makeScene(eventList));
    }

    private void showSeatMap(Long eventId, String eventName, Long userId) {
        SeatMapView seatMap = new SeatMapView(
                apiClient, eventId, eventName, userId, this::showEventList);
        primaryStage.setScene(makeScene(seatMap));
    }

    private void showMyTickets() {
        MyTicketsView ticketsView = new MyTicketsView(
                apiClient, currentUserId, this::showEventList);
        primaryStage.setScene(makeScene(ticketsView));
    }

    private void showAdminDashboard() {
        AdminDashboardView admin = new AdminDashboardView(apiClient);
        primaryStage.setScene(makeScene(admin));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
