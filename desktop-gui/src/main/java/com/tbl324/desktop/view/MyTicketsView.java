package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.model.TicketDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class MyTicketsView extends BorderPane {

    private final ApiClient apiClient;
    private final Long      userId;
    private final Runnable  onBack;

    private final TableView<TicketDTO> table       = new TableView<>();
    private final Label                statusLabel = new Label("Yükleniyor...");

    public MyTicketsView(ApiClient apiClient, Long userId, Runnable onBack) {
        this.apiClient = apiClient;
        this.userId    = userId;
        this.onBack    = onBack;
        buildUi();
        loadTickets();
    }

    @SuppressWarnings("unchecked")
    private void buildUi() {
        // ── Header ──────────────────────────────────────────────────────────
        Label title = new Label("Biletlerim");
        title.getStyleClass().add("header-title");
        title.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(title, Priority.ALWAYS);

        HBox header = new HBox(title);
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);

        // ── Table ────────────────────────────────────────────────────────────
        TableColumn<TicketDTO, Long>   idCol     = new TableColumn<>("Bilet No");
        TableColumn<TicketDTO, Long>   eventCol  = new TableColumn<>("Etkinlik ID");
        TableColumn<TicketDTO, Long>   seatCol   = new TableColumn<>("Koltuk ID");
        TableColumn<TicketDTO, String> statusCol = new TableColumn<>("Durum");

        idCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleLongProperty(c.getValue().id()).asObject());
        eventCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleLongProperty(c.getValue().eventId()).asObject());
        seatCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleLongProperty(c.getValue().seatId()).asObject());
        statusCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().status()));

        idCol.setPrefWidth(90);
        eventCol.setPrefWidth(110);
        seatCol.setPrefWidth(110);
        statusCol.setPrefWidth(150);

        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("badge");
                    badge.getStyleClass().add(badgeClass(status));
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        table.getColumns().addAll(idCol, eventCol, seatCol, statusCol);
        table.setPlaceholder(new Label("Henüz biletiniz yok"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ── Bottom bar ───────────────────────────────────────────────────────
        statusLabel.getStyleClass().add("status-label");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        Button refreshBtn = new Button("Yenile");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> loadTickets());

        Button backBtn = new Button("← Etkinliklere Dön");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> onBack.run());

        HBox bottom = new HBox(8, statusLabel, refreshBtn, backBtn);
        bottom.getStyleClass().add("bottom-bar");
        bottom.setAlignment(Pos.CENTER_LEFT);

        setTop(header);
        setCenter(table);
        setBottom(bottom);
    }

    private void loadTickets() {
        statusLabel.setText("Yükleniyor...");
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

    private static String badgeClass(String status) {
        return switch (status.toUpperCase()) {
            case "CONFIRMED" -> "badge-confirmed";
            case "PENDING"   -> "badge-pending";
            case "CANCELLED" -> "badge-cancelled";
            case "RESERVED"  -> "badge-reserved";
            default          -> "badge-pending";
        };
    }
}
