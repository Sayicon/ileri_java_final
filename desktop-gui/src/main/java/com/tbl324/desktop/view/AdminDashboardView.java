package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.model.EventDTO;
import com.tbl324.desktop.model.TicketDTO;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;

public class AdminDashboardView extends BorderPane {

    private final ApiClient apiClient;

    public AdminDashboardView(ApiClient apiClient) {
        this.apiClient = apiClient;
        buildUi();
    }

    private void buildUi() {
        Label title = new Label("Admin Paneli");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;"
                + "-fx-background-color: #1565C0; -fx-padding: 16px;");
        title.setMaxWidth(Double.MAX_VALUE);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(buildEventsTab(), buildTicketsTab());

        setTop(title);
        setCenter(tabs);
        setPadding(new Insets(0));
    }

    private Tab buildEventsTab() {
        Tab tab = new Tab("Etkinlikler");

        TableView<EventDTO> table = new TableView<>();
        TableColumn<EventDTO, Long>   idCol     = new TableColumn<>("ID");
        TableColumn<EventDTO, String> titleCol  = new TableColumn<>("Başlık");
        TableColumn<EventDTO, String> statusCol = new TableColumn<>("Durum");

        idCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().id()).asObject());
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));

        idCol.setPrefWidth(50);
        titleCol.setPrefWidth(300);
        statusCol.setPrefWidth(100);
        table.getColumns().addAll(idCol, titleCol, statusCol);
        table.setPlaceholder(new Label("Etkinlik yok"));

        Label statusLabel = new Label("Yükleniyor...");
        Button refreshBtn = new Button("Yenile");
        Button newEventBtn = new Button("+ Yeni Etkinlik");

        refreshBtn.setOnAction(e -> loadEvents(table, statusLabel));
        newEventBtn.setOnAction(e -> showCreateEventDialog(table, statusLabel));

        HBox bottom = new HBox(8, statusLabel, refreshBtn, newEventBtn);
        bottom.setPadding(new Insets(8));

        BorderPane content = new BorderPane(table);
        content.setBottom(bottom);
        BorderPane.setMargin(bottom, new Insets(4));

        tab.setContent(content);
        loadEvents(table, statusLabel);
        return tab;
    }

    private Tab buildTicketsTab() {
        Tab tab = new Tab("Tüm Rezervasyonlar");

        TableView<TicketDTO> table = new TableView<>();
        TableColumn<TicketDTO, Long>   idCol      = new TableColumn<>("Bilet No");
        TableColumn<TicketDTO, Long>   userCol    = new TableColumn<>("Kullanıcı ID");
        TableColumn<TicketDTO, Long>   eventCol   = new TableColumn<>("Etkinlik ID");
        TableColumn<TicketDTO, Long>   seatCol    = new TableColumn<>("Koltuk ID");
        TableColumn<TicketDTO, String> statusCol  = new TableColumn<>("Durum");

        idCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().id()).asObject());
        userCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().userId()).asObject());
        eventCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().eventId()).asObject());
        seatCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().seatId()).asObject());
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));

        idCol.setPrefWidth(80);
        userCol.setPrefWidth(100);
        eventCol.setPrefWidth(100);
        seatCol.setPrefWidth(100);
        statusCol.setPrefWidth(120);
        table.getColumns().addAll(idCol, userCol, eventCol, seatCol, statusCol);
        table.setPlaceholder(new Label("Rezervasyon yok"));

        Label statusLabel = new Label("Yükleniyor...");
        statusLabel.getStyleClass().add("status-label");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        Button confirmBtn = new Button("Onayla");
        confirmBtn.getStyleClass().add("btn-primary");
        confirmBtn.setDisable(true);
        confirmBtn.setOnAction(e -> {
            TicketDTO sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            Thread.ofVirtual().start(() -> {
                try {
                    apiClient.confirmTicket(sel.id(), "CASH");
                    javafx.application.Platform.runLater(() ->
                            loadAllTickets(table, statusLabel));
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                            new Alert(Alert.AlertType.ERROR, "Onaylama başarısız: " + ex.getMessage()).showAndWait());
                }
            });
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            confirmBtn.setDisable(sel == null || !"PENDING".equalsIgnoreCase(sel.status()));
        });

        Button refreshBtn = new Button("Yenile");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> loadAllTickets(table, statusLabel));

        HBox bottom = new HBox(8, statusLabel, refreshBtn, confirmBtn);
        bottom.setPadding(new Insets(8));

        BorderPane content = new BorderPane(table);
        content.setBottom(bottom);

        tab.setContent(content);
        loadAllTickets(table, statusLabel);
        return tab;
    }

    private void loadEvents(TableView<EventDTO> table, Label statusLabel) {
        Thread.ofVirtual().start(() -> {
            try {
                List<EventDTO> events = apiClient.getEvents();
                javafx.application.Platform.runLater(() -> {
                    table.setItems(FXCollections.observableArrayList(events));
                    statusLabel.setText(events.size() + " etkinlik");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Hata: " + ex.getMessage()));
            }
        });
    }

    private void loadAllTickets(TableView<TicketDTO> table, Label statusLabel) {
        Thread.ofVirtual().start(() -> {
            try {
                List<TicketDTO> tickets = apiClient.getAllTickets();
                javafx.application.Platform.runLater(() -> {
                    table.setItems(FXCollections.observableArrayList(tickets));
                    statusLabel.setText(tickets.size() + " rezervasyon");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Hata: " + ex.getMessage()));
            }
        });
    }

    private void showCreateEventDialog(TableView<EventDTO> table, Label statusLabel) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Yeni Etkinlik");
        dialog.setHeaderText("Etkinlik bilgilerini girin");

        ButtonType createBtn = new ButtonType("Oluştur", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        TextField titleField    = new TextField();
        TextField descField     = new TextField();
        TextField venueField    = new TextField("1");
        TextField startField    = new TextField("2027-12-01T20:00:00");
        TextField endField      = new TextField("2027-12-01T23:00:00");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));
        grid.add(new Label("Başlık:"),      0, 0); grid.add(titleField,  1, 0);
        grid.add(new Label("Açıklama:"),    0, 1); grid.add(descField,   1, 1);
        grid.add(new Label("Salon ID:"),    0, 2); grid.add(venueField,  1, 2);
        grid.add(new Label("Başlangıç:"),   0, 3); grid.add(startField,  1, 3);
        grid.add(new Label("Bitiş:"),       0, 4); grid.add(endField,    1, 4);

        dialog.getDialogPane().setContent(grid);
        javafx.application.Platform.runLater(titleField::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == createBtn) {
            Thread.ofVirtual().start(() -> {
                try {
                    long venueId = Long.parseLong(venueField.getText().trim());
                    apiClient.createEvent(
                            titleField.getText().trim(),
                            descField.getText().trim(),
                            venueId,
                            startField.getText().trim(),
                            endField.getText().trim());
                    javafx.application.Platform.runLater(() ->
                            loadEvents(table, statusLabel));
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                            statusLabel.setText("Hata: " + ex.getMessage()));
                }
            });
        }
    }
}
