package com.tbl324.mobile;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbl324.mobile.api.ApiClient;
import com.tbl324.mobile.model.TicketItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTicketsActivity extends Activity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        float d = getResources().getDisplayMetrics().density;
        int padH = (int) (16 * d);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFF5F5F5);

        // Header
        TextView header = new TextView(this);
        header.setText("Biletlerim");
        header.setTextSize(20);
        header.setTextColor(Color.WHITE);
        header.setTypeface(null, Typeface.BOLD);
        header.setBackgroundColor(0xFF1565C0);
        header.setPadding(padH, (int) (18 * d), padH, (int) (18 * d));
        root.addView(header);

        // Back button
        Button backBtn = new Button(this);
        backBtn.setText("← Etkinliklere Dön");
        backBtn.setOnClickListener(v -> finish());
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(padH, (int) (8 * d), padH, 0);
        root.addView(backBtn, btnParams);

        // Ticket list
        ScrollView scroll = new ScrollView(this);
        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(padH, (int) (8 * d), padH, (int) (8 * d));
        scroll.addView(container);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
        loadTickets();
    }

    private void loadTickets() {
        long userId = ApiClient.getInstance().getUserId();
        if (userId == -1L) {
            Toast.makeText(this, "Oturum bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiClient.getInstance().getService().getMyTickets(userId)
                .enqueue(new Callback<List<TicketItem>>() {
            @Override
            public void onResponse(Call<List<TicketItem>> call, Response<List<TicketItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TicketItem> confirmed = response.body().stream()
                            .filter(t -> "CONFIRMED".equalsIgnoreCase(t.getStatus()))
                            .collect(java.util.stream.Collectors.toList());
                    showTickets(confirmed);
                } else {
                    Toast.makeText(MyTicketsActivity.this,
                            "Yükleme hatası: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TicketItem>> call, Throwable t) {
                Toast.makeText(MyTicketsActivity.this, "Bağlantı hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTickets(List<TicketItem> tickets) {
        float d = getResources().getDisplayMetrics().density;
        int padH = (int) (16 * d);
        int padV = (int) (14 * d);
        int marginV = (int) (8 * d);

        container.removeAllViews();

        if (tickets.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Henüz biletiniz yok.");
            empty.setTextSize(15);
            empty.setTextColor(0xFF757575);
            empty.setPadding(0, (int) (24 * d), 0, 0);
            container.addView(empty);
            return;
        }

        for (TicketItem ticket : tickets) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(Color.WHITE);
            card.setPadding(padH, padV, padH, padV);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = marginV;

            TextView idText = new TextView(this);
            idText.setText("Bilet #" + ticket.getId());
            idText.setTextSize(16);
            idText.setTextColor(Color.BLACK);
            idText.setTypeface(null, Typeface.BOLD);
            card.addView(idText);

            TextView detailText = new TextView(this);
            detailText.setText("Etkinlik: " + ticket.getEventId() + "  |  Koltuk: " + ticket.getSeatId());
            detailText.setTextSize(13);
            detailText.setTextColor(0xFF424242);
            LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            dp.topMargin = (int) (4 * d);
            card.addView(detailText, dp);

            TextView statusText = new TextView(this);
            statusText.setText(ticket.getStatus());
            statusText.setTextSize(13);
            int statusColor = "CONFIRMED".equals(ticket.getStatus()) ? 0xFF2E7D32
                    : "PENDING".equals(ticket.getStatus()) ? 0xFFF57F17 : 0xFF757575;
            statusText.setTextColor(statusColor);
            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            sp.topMargin = (int) (2 * d);
            card.addView(statusText, sp);

            container.addView(card, cardParams);
        }
    }
}
