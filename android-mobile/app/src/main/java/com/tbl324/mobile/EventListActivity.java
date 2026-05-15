package com.tbl324.mobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbl324.mobile.api.ApiClient;
import com.tbl324.mobile.api.EventsResponse;
import com.tbl324.mobile.model.EventItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListActivity extends Activity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        float d = getResources().getDisplayMetrics().density;
        int padH = (int) (16 * d);
        int padV = (int) (14 * d);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFF5F5F5);

        // Header bar
        TextView header = new TextView(this);
        header.setText("Etkinlikler");
        header.setTextSize(20);
        header.setTextColor(Color.WHITE);
        header.setTypeface(null, Typeface.BOLD);
        header.setBackgroundColor(0xFF1565C0);
        header.setPadding(padH, (int) (18 * d), padH, (int) (18 * d));
        root.addView(header);

        // Biletlerim button
        Button ticketsBtn = new Button(this);
        ticketsBtn.setText("Biletlerim");
        ticketsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, MyTicketsActivity.class)));
        LinearLayout.LayoutParams ticketsBtnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ticketsBtnParams.setMargins(padH, (int) (6 * d), padH, 0);
        root.addView(ticketsBtn, ticketsBtnParams);

        // Scrollable list
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFFF5F5F5);
        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(padH, (int) (8 * d), padH, (int) (8 * d));
        scroll.addView(container);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
        loadEvents();
    }

    private void loadEvents() {
        ApiClient.getInstance().getService().getEvents().enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null
                        && response.body().getData().getContent() != null) {
                    showEvents(response.body().getData().getContent());
                } else {
                    Toast.makeText(EventListActivity.this,
                            "Yükleme hatası: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Toast.makeText(EventListActivity.this, "Bağlantı hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEvents(List<EventItem> events) {
        float d = getResources().getDisplayMetrics().density;
        int padH = (int) (16 * d);
        int padV = (int) (14 * d);
        int marginV = (int) (8 * d);

        container.removeAllViews();
        for (EventItem event : events) {
            // Card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(Color.WHITE);
            card.setPadding(padH, padV, padH, padV);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = marginV;

            // Title
            TextView title = new TextView(this);
            title.setText(event.getTitle());
            title.setTextSize(17);
            title.setTextColor(Color.BLACK);
            title.setTypeface(null, Typeface.BOLD);
            card.addView(title);

            // Status badge
            TextView status = new TextView(this);
            status.setText(event.getStatus());
            status.setTextSize(13);
            status.setTextColor("ACTIVE".equals(event.getStatus()) ? 0xFF2E7D32 : 0xFF757575);
            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            sp.topMargin = (int) (4 * d);
            card.addView(status, sp);

            card.setClickable(true);
            card.setFocusable(true);
            final EventItem e = event;
            card.setOnClickListener(v -> {
                Intent intent = new Intent(EventListActivity.this, SeatMapActivity.class);
                intent.putExtra("eventId", e.getId());
                intent.putExtra("eventName", e.getName());
                startActivity(intent);
            });

            container.addView(card, cardParams);
        }
    }
}
