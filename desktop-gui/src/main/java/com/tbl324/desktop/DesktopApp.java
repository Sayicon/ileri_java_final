package com.tbl324.desktop;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.view.EventListView;
import com.tbl324.desktop.view.LoginView;
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
        LoginView login = new LoginView((username, password) -> {
            // mock login: gerçek auth service çağrısı faz 6+ ile
            showEventList();
        });
        primaryStage.setScene(new Scene(login, 900, 650));
    }

    private void showEventList() {
        EventListView eventList = new EventListView(apiClient, currentUserId,
                (event, userId) -> showSeatMap(event.id(), userId));
        primaryStage.setScene(new Scene(eventList, 900, 650));
    }

    private void showSeatMap(Long eventId, Long userId) {
        SeatMapView seatMap = new SeatMapView(apiClient, eventId, userId);
        primaryStage.setScene(new Scene(seatMap, 900, 650));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
