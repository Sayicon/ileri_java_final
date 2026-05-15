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
import javafx.stage.Stage;

public class DesktopApp extends Application {

    private static final String GATEWAY_URL = System.getProperty(
            "gateway.url", "http://localhost:8080");

    private ApiClient apiClient;
    private Stage primaryStage;
    private Long currentUserId = 1L;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.apiClient    = new ApiClient(GATEWAY_URL);

        stage.setTitle("TBL324 Event Ticketing");
        stage.setWidth(900);
        stage.setHeight(650);
        showLogin();
        stage.show();
    }

    private void showLogin() {
        LoginView[] ref = new LoginView[1];
        LoginView login = new LoginView((username, password) -> {
            try {
                apiClient.login(username, password);
                currentUserId = apiClient.getUserId();
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
        primaryStage.setScene(new Scene(login, 900, 650));
    }

    private void showEventList() {
        EventListView eventList = new EventListView(apiClient, currentUserId,
                (event, userId) -> showSeatMap(event.id(), userId),
                this::showMyTickets);
        primaryStage.setScene(new Scene(eventList, 900, 650));
    }

    private void showSeatMap(Long eventId, Long userId) {
        SeatMapView seatMap = new SeatMapView(apiClient, eventId, userId);
        primaryStage.setScene(new Scene(seatMap, 900, 650));
    }

    private void showMyTickets() {
        MyTicketsView ticketsView = new MyTicketsView(apiClient, currentUserId, this::showEventList);
        primaryStage.setScene(new Scene(ticketsView, 900, 650));
    }

    private void showAdminDashboard() {
        AdminDashboardView admin = new AdminDashboardView(apiClient);
        primaryStage.setScene(new Scene(admin, 900, 650));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
