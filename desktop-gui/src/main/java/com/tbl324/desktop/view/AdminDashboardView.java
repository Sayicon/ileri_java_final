package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.model.EventDTO;
import com.tbl324.desktop.model.TicketDTO;
import com.tbl324.desktop.model.VenueDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminDashboardView extends BorderPane {

    private final ApiClient apiClient;
    private final Runnable  onLogout;

    public AdminDashboardView(ApiClient apiClient, Runnable onLogout) {
        this.apiClient = apiClient;
        this.onLogout  = onLogout;
        buildUi();
    }

    private void buildUi() {
        Label title = new Label("Admin Paneli");
        title.getStyleClass().add("header-title");
        title.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(title, Priority.ALWAYS);

        Button logoutBtn = new Button("Çıkış");
        logoutBtn.getStyleClass().add("btn-ghost");
        logoutBtn.setOnAction(e -> {
            Thread.ofVirtual().start(() -> {
                try { apiClient.logout(); } catch (Exception ignored) {}
                javafx.application.Platform.runLater(onLogout);
            });
        });

        HBox header = new HBox(title, logoutBtn);
        header.getStyleClass().add("header-bar");
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(buildEventsTab(), buildTicketsTab(), buildVenuesTab());

        setTop(header);
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
        List<VenueDTO> venues;
        try {
            venues = apiClient.getVenues();
        } catch (Exception e) {
            statusLabel.setText("Salonlar yüklenemedi: " + e.getMessage());
            return;
        }
        if (venues.isEmpty()) {
            statusLabel.setText("Hiç salon bulunamadı.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Yeni Etkinlik");
        dialog.setHeaderText("Etkinlik bilgilerini girin");

        ButtonType createBtn = new ButtonType("Oluştur", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        TextField titleField = new TextField();
        TextField descField  = new TextField();

        ComboBox<VenueDTO> venueBox = new ComboBox<>(FXCollections.observableArrayList(venues));
        venueBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(VenueDTO v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.id() + " — " + v.name() + " (" + v.capacity() + " kişi)");
            }
        });
        venueBox.setButtonCell(venueBox.getCellFactory().call(null));
        venueBox.getSelectionModel().selectFirst();
        venueBox.setMaxWidth(Double.MAX_VALUE);

        // Tarih seçici + saat Spinner
        DatePicker startDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        Spinner<Integer> startHourSpinner = hourSpinner(20);
        Spinner<Integer> startMinSpinner  = minuteSpinner(0);

        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        Spinner<Integer> endHourSpinner = hourSpinner(23);
        Spinner<Integer> endMinSpinner  = minuteSpinner(0);

        HBox startBox = new HBox(6, startDatePicker, new Label("Saat:"), startHourSpinner, new Label(":"), startMinSpinner);
        startBox.setAlignment(Pos.CENTER_LEFT);
        HBox endBox   = new HBox(6, endDatePicker,   new Label("Saat:"), endHourSpinner,   new Label(":"), endMinSpinner);
        endBox.setAlignment(Pos.CENTER_LEFT);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12px;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));
        grid.add(new Label("Başlık:"),    0, 0); grid.add(titleField, 1, 0);
        grid.add(new Label("Açıklama:"), 0, 1); grid.add(descField,  1, 1);
        grid.add(new Label("Salon:"),    0, 2); grid.add(venueBox,   1, 2);
        grid.add(new Label("Başlangıç:"),0, 3); grid.add(startBox,   1, 3);
        grid.add(new Label("Bitiş:"),    0, 4); grid.add(endBox,     1, 4);
        grid.add(errorLabel,             0, 5, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(520);
        javafx.application.Platform.runLater(titleField::requestFocus);

        // "Oluştur" butonunu; tarih/saat doğrulamadan geçmezse kapat
        javafx.scene.Node okBtn = dialog.getDialogPane().lookupButton(createBtn);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (titleField.getText().trim().isEmpty()) {
                errorLabel.setText("Başlık boş olamaz.");
                event.consume();
                return;
            }
            LocalDateTime start = toDateTime(startDatePicker, startHourSpinner, startMinSpinner);
            LocalDateTime end   = toDateTime(endDatePicker,   endHourSpinner,   endMinSpinner);
            if (!end.isAfter(start)) {
                errorLabel.setText("Bitiş tarihi başlangıçtan sonra olmalı.");
                event.consume();
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == createBtn) {
            VenueDTO selected = venueBox.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            LocalDateTime start = toDateTime(startDatePicker, startHourSpinner, startMinSpinner);
            LocalDateTime end   = toDateTime(endDatePicker,   endHourSpinner,   endMinSpinner);
            String startIso = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String endIso   = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Thread.ofVirtual().start(() -> {
                try {
                    apiClient.createEvent(
                            titleField.getText().trim(),
                            descField.getText().trim(),
                            selected.id(),
                            startIso, endIso);
                    javafx.application.Platform.runLater(() -> loadEvents(table, statusLabel));
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                            statusLabel.setText("Hata: " + ex.getMessage()));
                }
            });
        }
    }

    private static Spinner<Integer> hourSpinner(int initial) {
        Spinner<Integer> s = new Spinner<>(0, 23, initial);
        s.setEditable(true);
        s.setPrefWidth(65);
        return s;
    }

    private static Spinner<Integer> minuteSpinner(int initial) {
        Spinner<Integer> s = new Spinner<>(0, 59, initial, 5);
        s.setEditable(true);
        s.setPrefWidth(65);
        return s;
    }

    private static LocalDateTime toDateTime(DatePicker dp, Spinner<Integer> hour, Spinner<Integer> min) {
        LocalDate date = dp.getValue() != null ? dp.getValue() : LocalDate.now().plusDays(1);
        return date.atTime(hour.getValue(), min.getValue());
    }

    private Tab buildVenuesTab() {
        Tab tab = new Tab("Salonlar");

        TableView<VenueDTO> table = new TableView<>();
        TableColumn<VenueDTO, Long>   idCol       = new TableColumn<>("ID");
        TableColumn<VenueDTO, String> nameCol     = new TableColumn<>("Salon Adı");
        TableColumn<VenueDTO, String> addressCol  = new TableColumn<>("Adres");
        TableColumn<VenueDTO, Long>   capacityCol = new TableColumn<>("Kapasite");

        idCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().id()).asObject());
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        addressCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().address()));
        capacityCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().capacity()).asObject());

        idCol.setPrefWidth(50);
        nameCol.setPrefWidth(220);
        addressCol.setPrefWidth(250);
        capacityCol.setPrefWidth(90);
        table.getColumns().addAll(idCol, nameCol, addressCol, capacityCol);
        table.setPlaceholder(new Label("Salon yok"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label statusLabel = new Label("Yükleniyor...");
        statusLabel.getStyleClass().add("status-label");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        Button refreshBtn = new Button("Yenile");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> loadVenues(table, statusLabel));

        HBox bottom = new HBox(8, statusLabel, refreshBtn);
        bottom.setPadding(new Insets(8));

        BorderPane content = new BorderPane(table);
        content.setBottom(bottom);
        tab.setContent(content);
        loadVenues(table, statusLabel);
        return tab;
    }

    private void loadVenues(TableView<VenueDTO> table, Label statusLabel) {
        Thread.ofVirtual().start(() -> {
            try {
                List<VenueDTO> venues = apiClient.getVenues();
                javafx.application.Platform.runLater(() -> {
                    table.setItems(FXCollections.observableArrayList(venues));
                    statusLabel.setText(venues.size() + " salon");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Hata: " + ex.getMessage()));
            }
        });
    }
}
