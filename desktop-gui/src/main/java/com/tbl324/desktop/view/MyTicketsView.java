package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.model.TicketDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class MyTicketsView extends BorderPane {

    private final ApiClient apiClient;
    private final Long userId;
    private final Runnable onBack;

    private final TableView<TicketDTO> table = new TableView<>();
    private final Label statusLabel = new Label("Biletler yükleniyor...");

    public MyTicketsView(ApiClient apiClient, Long userId, Runnable onBack) {
        this.apiClient = apiClient;
        this.userId    = userId;
        this.onBack    = onBack;
        buildUi();
        loadTickets();
    }

    @SuppressWarnings("unchecked")
    private void buildUi() {
        TableColumn<TicketDTO, Long>   idCol      = new TableColumn<>("Bilet No");
        TableColumn<TicketDTO, Long>   eventCol   = new TableColumn<>("Etkinlik ID");
        TableColumn<TicketDTO, Long>   seatCol    = new TableColumn<>("Koltuk ID");
        TableColumn<TicketDTO, String> statusCol  = new TableColumn<>("Durum");

        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleLongProperty(c.getValue().id()).asObject());
        eventCol.setCellValueFactory(c -> new javafx.beans.property.SimpleLongProperty(c.getValue().eventId()).asObject());
        seatCol.setCellValueFactory(c -> new javafx.beans.property.SimpleLongProperty(c.getValue().seatId()).asObject());
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().status()));

        idCol.setPrefWidth(80);
        eventCol.setPrefWidth(100);
        seatCol.setPrefWidth(100);
        statusCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, eventCol, seatCol, statusCol);
        table.setPlaceholder(new Label("Henüz biletiniz yok"));

        Button backBtn    = new Button("← Etkinliklere Dön");
        Button refreshBtn = new Button("Yenile");
        backBtn.setOnAction(e -> onBack.run());
        refreshBtn.setOnAction(e -> loadTickets());

        HBox bottom = new HBox(8, statusLabel, refreshBtn, backBtn);
        bottom.setPadding(new Insets(8));

        setCenter(table);
        setBottom(bottom);
        setPadding(new Insets(12));
    }

    private void loadTickets() {
        Thread.ofVirtual().start(() -> {
            try {
                List<TicketDTO> tickets = apiClient.getMyTickets(userId);
                javafx.application.Platform.runLater(() -> {
                    table.setItems(FXCollections.observableArrayList(tickets));
                    statusLabel.setText(tickets.size() + " bilet");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Hata: " + ex.getMessage()));
            }
        });
    }
}
