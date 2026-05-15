package com.tbl324.desktop.view;

import com.tbl324.desktop.client.ApiClient;
import com.tbl324.desktop.model.SeatColorMapper;
import com.tbl324.desktop.model.SeatDTO;
import com.tbl324.desktop.model.SeatGrid;
import com.tbl324.desktop.model.SeatStatus;
import com.tbl324.desktop.model.TicketDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatMapView extends BorderPane {

    private static final double CELL_SIZE = 44.0;
    private static final double CELL_GAP  = 4.0;
    private static final int    COLS      = 10;

    private final ApiClient apiClient;
    private final Long      eventId;
    private final String    eventName;
    private final Long      userId;
    private final Runnable  onBack;

    private SeatGrid          grid     = SeatGrid.fromSeats(List.of(), COLS);
    private final List<SeatDTO> selected = new ArrayList<>();

    private Canvas canvas;
    private Label  selectionLabel;

    public SeatMapView(ApiClient apiClient, Long eventId, String eventName,
                       Long userId, Runnable onBack) {
        this.apiClient = apiClient;
        this.eventId   = eventId;
        this.eventName = eventName;
        this.userId    = userId;
        this.onBack    = onBack;
        buildUi();
        loadSeats();
    }

    private void buildUi() {
        // ── Header ──────────────────────────────────────────────────────────
        Button backBtn = new Button("← Geri");
        backBtn.getStyleClass().add("btn-ghost");
        backBtn.setOnAction(e -> onBack.run());

        Label titleLabel = new Label(eventName);
        titleLabel.getStyleClass().add("header-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        selectionLabel = new Label("Koltuk seçin");
        selectionLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 13px;");

        HBox header = new HBox(12, backBtn, titleLabel, selectionLabel);
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);

        // ── Legend ──────────────────────────────────────────────────────────
        HBox legend = new HBox(16);
        legend.getStyleClass().add("legend-bar");
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
                legendItem("#4CAF50", "Müsait"),
                legendItem("#2196F3", "Seçili"),
                legendItem("#F44336", "Dolu")
        );

        VBox topSection = new VBox(header, legend);

        // ── Canvas ──────────────────────────────────────────────────────────
        canvas = new Canvas(COLS * (CELL_SIZE + CELL_GAP), 600);
        canvas.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));

        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(false);
        scroll.setStyle("-fx-background-color: #F5F5F5; -fx-background: #F5F5F5;");
        scroll.setPadding(new Insets(12));

        // ── Bottom bar ───────────────────────────────────────────────────────
        Button reserveBtn = new Button("Rezerve Et");
        reserveBtn.getStyleClass().add("btn-primary");
        reserveBtn.setMaxWidth(Double.MAX_VALUE);
        reserveBtn.setOnAction(e -> doReserve());

        HBox bottom = new HBox(reserveBtn);
        bottom.getStyleClass().add("bottom-bar");
        bottom.setPadding(new Insets(12, 16, 12, 16));
        HBox.setHgrow(reserveBtn, Priority.ALWAYS);

        setTop(topSection);
        setCenter(scroll);
        setBottom(bottom);
    }

    private HBox legendItem(String hexColor, String text) {
        Pane dot = new Pane();
        dot.setStyle("-fx-background-color: " + hexColor + "; -fx-background-radius: 50%;");
        dot.setMinSize(12, 12);
        dot.setMaxSize(12, 12);

        Label lbl = new Label(text);
        lbl.getStyleClass().add("legend-text");

        HBox box = new HBox(6, dot, lbl);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void loadSeats() {
        Thread.ofVirtual().start(() -> {
            try {
                List<SeatDTO> seats = apiClient.getSeats(eventId);
                grid = SeatGrid.fromSeats(seats, COLS);
                javafx.application.Platform.runLater(this::render);
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() ->
                        selectionLabel.setText("Koltuklar yüklenemedi"));
            }
        });
    }

    private void render() {
        double step   = CELL_SIZE + CELL_GAP;
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

                SeatDTO    seat   = opt.get();
                SeatStatus status = selected.contains(seat) ? SeatStatus.SELECTED : seat.status();
                java.awt.Color awt = SeatColorMapper.colorFor(status);
                gc.setFill(Color.rgb(awt.getRed(), awt.getGreen(), awt.getBlue()));
                gc.fillRoundRect(x, y, CELL_SIZE, CELL_SIZE, 8, 8);

                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font(11));
                gc.fillText(String.valueOf(seat.id()), x + 6, y + 27);
            }
        }
    }

    private void handleClick(double px, double py) {
        double step = CELL_SIZE + CELL_GAP;
        grid.atPixel(px - CELL_GAP, py - CELL_GAP, step).ifPresent(seat -> {
            if (seat.status() != SeatStatus.AVAILABLE) return;
            if (selected.contains(seat)) selected.remove(seat);
            else selected.add(seat);
            selectionLabel.setText(selected.isEmpty()
                    ? "Koltuk seçin"
                    : selected.size() + " koltuk seçildi");
            render();
        });
    }

    private void doReserve() {
        if (selected.isEmpty()) {
            selectionLabel.setText("Önce bir koltuk seçin");
            return;
        }
        Thread.ofVirtual().start(() -> {
            List<String> errors    = new ArrayList<>();
            List<Long>   reserved  = new ArrayList<>();
            for (SeatDTO seat : selected) {
                try {
                    TicketDTO ticket = apiClient.reserve(eventId, seat.id(), userId);
                    reserved.add(ticket.id());
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
                showPaymentDialog(reserved);
            });
        });
    }

    private void showPaymentDialog(List<Long> ticketIds) {
        ButtonType nakit = new ButtonType("Nakit");
        ButtonType kredi = new ButtonType("Kredi Kartı");
        ButtonType iptal = new ButtonType("İptal");

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Ödeme Yöntemi");
        dialog.setHeaderText(ticketIds.size() + " bilet rezerve edildi.");
        dialog.setContentText("Ödeme yöntemini seçin:");
        dialog.getButtonTypes().setAll(nakit, kredi, iptal);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == iptal) { onBack.run(); return; }
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
                    if (!errors.isEmpty()) {
                        new Alert(Alert.AlertType.ERROR, String.join("\n", errors)).showAndWait();
                    }
                    onBack.run();
                });
            });
        });
    }
}
