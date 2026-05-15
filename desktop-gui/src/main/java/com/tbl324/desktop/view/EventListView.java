package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.model.EventDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.BiConsumer;

public class EventListView extends BorderPane {

    private final ApiClient apiClient;
    private final Long userId;
    private final BiConsumer<EventDTO, Long> onEventSelected;
    private final Runnable onMyTickets;

    private final ListView<EventDTO> listView = new ListView<>();
    private final Label statusLabel = new Label("Etkinlikler yükleniyor...");

    public EventListView(ApiClient apiClient, Long userId,
                         BiConsumer<EventDTO, Long> onEventSelected,
                         Runnable onMyTickets) {
        this.apiClient       = apiClient;
        this.userId          = userId;
        this.onEventSelected = onEventSelected;
        this.onMyTickets     = onMyTickets;
        buildUi();
        loadEvents();
    }

    private void buildUi() {
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(EventDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.title() + " — " + item.status());
            }
        });

        Button selectBtn = new Button("Koltuğa Git");
        selectBtn.setOnAction(e -> {
            EventDTO selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) onEventSelected.accept(selected, userId);
        });

        Button refreshBtn = new Button("Yenile");
        refreshBtn.setOnAction(e -> loadEvents());

        Button ticketsBtn = new Button("Biletlerim");
        ticketsBtn.setOnAction(e -> onMyTickets.run());

        HBox bottom = new HBox(8, statusLabel, refreshBtn, selectBtn, ticketsBtn);
        bottom.setPadding(new Insets(8));

        setCenter(listView);
        setBottom(bottom);
        setPadding(new Insets(12));
    }

    private void loadEvents() {
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
}
