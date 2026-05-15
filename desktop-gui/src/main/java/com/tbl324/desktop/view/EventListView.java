package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.model.EventDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.function.BiConsumer;

public class EventListView extends BorderPane {

    private final ApiClient apiClient;
    private final Long userId;
    private final String username;
    private final BiConsumer<EventDTO, Long> onEventSelected;
    private final Runnable onMyTickets;

    private final ListView<EventDTO> listView    = new ListView<>();
    private final Label              statusLabel = new Label("Yükleniyor...");

    public EventListView(ApiClient apiClient, Long userId, String username,
                         BiConsumer<EventDTO, Long> onEventSelected,
                         Runnable onMyTickets) {
        this.apiClient       = apiClient;
        this.userId          = userId;
        this.username        = username;
        this.onEventSelected = onEventSelected;
        this.onMyTickets     = onMyTickets;
        buildUi();
        loadEvents();
    }

    private void buildUi() {
        // ── Header ─────────────────────────────────────────────────────────
        HBox header = new HBox();
        header.getStyleClass().add("header-bar");

        Label title = new Label("Etkinlikler");
        title.getStyleClass().add("header-title");
        HBox.setHgrow(title, Priority.ALWAYS);

        Label userLabel = new Label("👤 " + username);
        userLabel.getStyleClass().add("header-sub");
        userLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 13px;");

        header.getChildren().addAll(title, userLabel);

        // ── List with custom cells ──────────────────────────────────────────
        listView.getStyleClass().add("list-view");
        listView.setCellFactory(lv -> new EventCell());
        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                EventDTO sel = listView.getSelectionModel().getSelectedItem();
                if (sel != null) onEventSelected.accept(sel, userId);
            }
        });

        // ── Bottom bar ──────────────────────────────────────────────────────
        statusLabel.getStyleClass().add("status-label");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        Button refreshBtn = new Button("Yenile");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> loadEvents());

        Button ticketsBtn = new Button("Biletlerim");
        ticketsBtn.getStyleClass().add("btn-secondary");
        ticketsBtn.setOnAction(e -> onMyTickets.run());

        Button selectBtn = new Button("Koltuğa Git →");
        selectBtn.getStyleClass().add("btn-primary");
        selectBtn.setOnAction(e -> {
            EventDTO sel = listView.getSelectionModel().getSelectedItem();
            if (sel != null) onEventSelected.accept(sel, userId);
            else statusLabel.setText("Önce bir etkinlik seçin.");
        });

        HBox bottom = new HBox(8, statusLabel, refreshBtn, ticketsBtn, selectBtn);
        bottom.getStyleClass().add("bottom-bar");
        bottom.setAlignment(Pos.CENTER_LEFT);

        setTop(header);
        setCenter(listView);
        setBottom(bottom);
    }

    private void loadEvents() {
        statusLabel.setText("Yükleniyor...");
        Thread.ofVirtual().start(() -> {
            try {
                List<EventDTO> events = apiClient.getEvents();
                javafx.application.Platform.runLater(() -> {
                    listView.setItems(FXCollections.observableArrayList(events));
                    statusLabel.setText(events.size() + " etkinlik");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Hata: " + ex.getMessage()));
            }
        });
    }

    // ── Custom list cell ───────────────────────────────────────────────────
    private class EventCell extends ListCell<EventDTO> {
        private final VBox  box        = new VBox(4);
        private final Label titleLabel = new Label();
        private final Label descLabel  = new Label();
        private final Label badge      = new Label();
        private final HBox  topRow     = new HBox(8);

        EventCell() {
            titleLabel.getStyleClass().add("event-title");
            descLabel.getStyleClass().add("event-desc");
            badge.getStyleClass().addAll("badge");

            topRow.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(titleLabel, Priority.ALWAYS);
            topRow.getChildren().addAll(titleLabel, badge);

            box.getChildren().addAll(topRow, descLabel);
            box.setMaxWidth(Double.MAX_VALUE);
            setPadding(Insets.EMPTY);
        }

        @Override
        protected void updateItem(EventDTO item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                titleLabel.setText(item.title());
                descLabel.setText(item.description() != null ? item.description() : "");

                badge.setText(item.status());
                badge.getStyleClass().removeAll("badge-active", "badge-cancelled", "badge-pending");
                if ("ACTIVE".equalsIgnoreCase(item.status())) {
                    badge.getStyleClass().add("badge-active");
                } else {
                    badge.getStyleClass().add("badge-cancelled");
                }

                boolean selected = isSelected();
                box.getStyleClass().setAll(selected ? "event-card-selected" : "event-card");
                setGraphic(box);
                setText(null);
            }
        }
    }
}
