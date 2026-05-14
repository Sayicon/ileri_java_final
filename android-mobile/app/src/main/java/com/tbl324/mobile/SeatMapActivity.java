package com.tbl324.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbl324.mobile.api.ApiClient;
import com.tbl324.mobile.model.SeatGridModel;
import com.tbl324.mobile.model.SeatItem;
import com.tbl324.mobile.viewmodel.SeatViewModel;
import com.tbl324.mobile.views.SeatMapView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeatMapActivity extends Activity {

    private static final int COLS = 10;

    private SeatViewModel viewModel;
    private SeatMapView seatMapView;
    private TextView selectionLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long eventId   = getIntent().getLongExtra("eventId", -1L);
        String name    = getIntent().getStringExtra("eventName");

        viewModel = new SeatViewModel();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText(name != null ? name : "Etkinlik");
        root.addView(title);

        selectionLabel = new TextView(this);
        selectionLabel.setText("Seçili: 0");
        root.addView(selectionLabel);

        seatMapView = new SeatMapView(this);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(seatMapView);
        root.addView(scroll);

        Button reserveBtn = new Button(this);
        reserveBtn.setText("Rezerve Et");
        reserveBtn.setOnClickListener(v -> reserve(eventId));
        root.addView(reserveBtn);

        setContentView(root);

        seatMapView.setOnSeatClickListener((seat, nowSelected) ->
                selectionLabel.setText("Seçili: " + viewModel.getSelectedCount()));

        if (eventId != -1L) loadSeats(eventId);
    }

    private void loadSeats(long eventId) {
        ApiClient.getInstance().getService().getSeats(eventId).enqueue(new Callback<List<SeatItem>>() {
            @Override
            public void onResponse(Call<List<SeatItem>> call, Response<List<SeatItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SeatGridModel grid = SeatGridModel.fromSeats(response.body(), COLS);
                    seatMapView.setGrid(grid, viewModel);
                } else {
                    Toast.makeText(SeatMapActivity.this, "Koltuk yüklenemedi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SeatItem>> call, Throwable t) {
                Toast.makeText(SeatMapActivity.this, "Bağlantı hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reserve(long eventId) {
        List<SeatItem> seats = viewModel.getSelected();
        if (seats.isEmpty()) {
            Toast.makeText(this, "Koltuk seçin", Toast.LENGTH_SHORT).show();
            return;
        }
        for (SeatItem seat : seats) {
            Map<String, Long> body = new HashMap<>();
            body.put("eventId", eventId);
            body.put("seatId", seat.getId());
            ApiClient.getInstance().getService().reserve(body).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) {}
                @Override public void onFailure(Call<Void> call, Throwable t) {}
            });
        }
        Toast.makeText(this, seats.size() + " koltuk rezerve edildi", Toast.LENGTH_SHORT).show();
        viewModel.clearSelection();
        selectionLabel.setText("Seçili: 0");
    }
}
