package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.model.SeatColorMapper;
import com.tbl324.desktop.model.SeatDTO;
import com.tbl324.desktop.model.SeatGrid;
import com.tbl324.desktop.model.SeatStatus;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import com.tbl324.desktop.model.TicketDTO;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatMapView extends BorderPane {

    private static final double CELL_SIZE = 44.0;
    private static final double CELL_GAP  = 4.0;
    private static final int    COLS      = 10;

    private final ApiClient apiClient;
    private final Long eventId;
    private final Long userId;

    private SeatGrid grid = SeatGrid.fromSeats(List.of(), COLS);
    private final List<SeatDTO> selected = new ArrayList<>();

    private Canvas canvas;
    private Label statusLabel;

    public SeatMapView(ApiClient apiClient, Long eventId, Long userId) {
        this.apiClient = apiClient;
        this.eventId   = eventId;
        this.userId    = userId;
        buildUi();
        loadSeats();
    }

    private void buildUi() {
        canvas = new Canvas(COLS * (CELL_SIZE + CELL_GAP), 600);
        canvas.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));
        canvas.setOnScroll(e -> {
            // scroll destekleniyor — gelecek implementasyon için hazır
        });

        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(true);

        statusLabel = new Label("Koltuk seçin");

        Button reserveBtn = new Button("Rezerve Et");
        reserveBtn.setOnAction(e -> doReserve());

        HBox bottom = new HBox(12, statusLabel, reserveBtn);
        bottom.setPadding(new Insets(8));

        setCenter(scroll);
        setBottom(bottom);
    }

    private void loadSeats() {
        Thread.ofVirtual().start(() -> {
            try {
                List<SeatDTO> seats = apiClient.getSeats(eventId);
                grid = SeatGrid.fromSeats(seats, COLS);
                javafx.application.Platform.runLater(this::render);
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Koltuklar yüklenemedi: " + ex.getMessage()));
            }
        });
    }

    private void render() {
        double step = CELL_SIZE + CELL_GAP;
        double height = grid.rows() * step + CELL_GAP;
        canvas.setHeight(Math.max(height, 600));

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int r = 0; r < grid.rows(); r++) {
            for (int c = 0; c < grid.cols(); c++) {
                double x = c * step + CELL_GAP;
                double y = r * step + CELL_GAP;

                Optional<SeatDTO> opt = grid.at(r, c);
                if (opt.isEmpty()) continue;

                SeatDTO seat = opt.get();
                SeatStatus status = selected.contains(seat) ? SeatStatus.SELECTED : seat.status();
                java.awt.Color awt = SeatColorMapper.colorFor(status);
                gc.setFill(Color.rgb(awt.getRed(), awt.getGreen(), awt.getBlue()));
                gc.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 8, 8);

                gc.setFill(Color.WHITE);
                gc.fillText(String.valueOf(seat.id()), x + 14, y + 26);
            }
        }
    }

    private void handleClick(double px, double py) {
        double step = CELL_SIZE + CELL_GAP;
        grid.atPixel(px - CELL_GAP, py - CELL_GAP, step).ifPresent(seat -> {
            if (seat.status() != SeatStatus.AVAILABLE) return;
            if (selected.contains(seat)) {
                selected.remove(seat);
            } else {
                selected.add(seat);
            }
            statusLabel.setText(selected.size() + " koltuk seçildi");
            render();
        });
    }

    private void doReserve() {
        if (selected.isEmpty()) {
            statusLabel.setText("Önce bir koltuk seçin.");
            return;
        }
        Thread.ofVirtual().start(() -> {
            List<String> errors = new ArrayList<>();
            List<Long> reservedIds = new ArrayList<>();
            for (SeatDTO seat : selected) {
                try {
                    TicketDTO ticket = apiClient.reserve(eventId, seat.id(), userId);
                    reservedIds.add(ticket.id());
                } catch (Exception ex) {
                    errors.add("Koltuk " + seat.id() + ": " + ex.getMessage());
                }
            }
            javafx.application.Platform.runLater(() -> {
                selected.clear();
                if (!errors.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, String.join("\n", errors)).showAndWait();
                    loadSeats();
                    return;
                }
                showPaymentDialog(reservedIds);
            });
        });
    }

    private void showPaymentDialog(List<Long> ticketIds) {
        ButtonType nakit  = new ButtonType("Nakit");
        ButtonType kredi  = new ButtonType("Kredi Kartı");
        ButtonType iptal  = new ButtonType("İptal");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Ödeme");
        dialog.setHeaderText(ticketIds.size() + " bilet rezerve edildi.");
        dialog.setContentText("Ödeme yöntemi seçin:");
        dialog.getButtonTypes().setAll(nakit, kredi, iptal);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == iptal) { loadSeats(); return; }
            String paymentType = (btn == nakit) ? "CASH" : "CREDIT_CARD";
            Thread.ofVirtual().start(() -> {
                List<String> errors = new ArrayList<>();
                for (Long id : ticketIds) {
                    try {
                        apiClient.confirmTicket(id, paymentType);
                    } catch (Exception ex) {
                        errors.add("Bilet " + id + ": " + ex.getMessage());
                    }
                }
                javafx.application.Platform.runLater(() -> {
                    loadSeats();
                    if (errors.isEmpty()) {
                        statusLabel.setText("Biletiniz onaylandı!");
                    } else {
                        new Alert(Alert.AlertType.ERROR, String.join("\n", errors)).showAndWait();
                    }
                });
            });
        });
    }
}
