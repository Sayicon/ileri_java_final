package com.tbl324.mobile;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbl324.mobile.api.ApiClient;
import com.tbl324.mobile.api.SeatsResponse;
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

    private SeatViewModel viewModel;
    private SeatMapView seatMapView;
    private TextView selectionLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        long eventId = getIntent().getLongExtra("eventId", -1L);
        String name  = getIntent().getStringExtra("eventName");

        viewModel = new SeatViewModel();

        float d = getResources().getDisplayMetrics().density;
        int padH = (int) (16 * d);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFF5F5F5);

        // Header
        TextView header = new TextView(this);
        header.setText(name != null ? name : "Koltuk Haritası");
        header.setTextSize(18);
        header.setTextColor(Color.WHITE);
        header.setTypeface(null, Typeface.BOLD);
        header.setBackgroundColor(0xFF1565C0);
        header.setPadding(padH, (int) (18 * d), padH, (int) (18 * d));
        root.addView(header);

        // Selection label
        selectionLabel = new TextView(this);
        selectionLabel.setText("Seçili koltuk: 0");
        selectionLabel.setTextSize(15);
        selectionLabel.setTextColor(Color.BLACK);
        selectionLabel.setBackgroundColor(Color.WHITE);
        selectionLabel.setPadding(padH, (int) (10 * d), padH, (int) (10 * d));
        root.addView(selectionLabel);

        // Seat map — both horizontal and vertical scrollable
        seatMapView = new SeatMapView(this);
        HorizontalScrollView hScroll = new HorizontalScrollView(this);
        hScroll.setFillViewport(false);
        hScroll.addView(seatMapView);
        ScrollView vScroll = new ScrollView(this);
        vScroll.setFillViewport(false);
        vScroll.addView(hScroll);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        root.addView(vScroll, scrollParams);

        // Reserve button
        Button reserveBtn = new Button(this);
        reserveBtn.setText("Rezerve Et");
        reserveBtn.setTextColor(Color.WHITE);
        reserveBtn.setBackgroundColor(0xFF1565C0);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(padH, (int) (8 * d), padH, (int) (16 * d));
        reserveBtn.setOnClickListener(v -> reserve(eventId));
        root.addView(reserveBtn, btnParams);

        setContentView(root);

        seatMapView.setOnSeatClickListener((seat, nowSelected) ->
                selectionLabel.setText("Seçili koltuk: " + viewModel.getSelectedCount()));

        if (eventId != -1L) loadSeats(eventId);
    }

    private void loadSeats(long eventId) {
        ApiClient.getInstance().getService().getSeats(eventId).enqueue(new Callback<SeatsResponse>() {
            @Override
            public void onResponse(Call<SeatsResponse> call, Response<SeatsResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    SeatGridModel grid = SeatGridModel.fromSeats(response.body().getData());
                    seatMapView.setGrid(grid, viewModel);
                } else {
                    Toast.makeText(SeatMapActivity.this, "Koltuk yüklenemedi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SeatsResponse> call, Throwable t) {
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
        Toast.makeText(this, seats.size() + " koltuk rezerve edildi", Toast.LENGTH_LONG).show();
        viewModel.clearSelection();
        selectionLabel.setText("Seçili koltuk: 0");
    }
}
