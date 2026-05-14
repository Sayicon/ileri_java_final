package com.tbl324.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tbl324.mobile.api.ApiClient;
import com.tbl324.mobile.model.EventItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListActivity extends Activity {

    private final List<EventItem> events = new ArrayList<>();
    private ArrayAdapter<EventItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = new ListView(this);
        setContentView(listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, events);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            EventItem event = events.get(position);
            Intent intent = new Intent(this, SeatMapActivity.class);
            intent.putExtra("eventId", event.getId());
            intent.putExtra("eventName", event.getName());
            startActivity(intent);
        });

        loadEvents();
    }

    private void loadEvents() {
        ApiClient.getInstance().getService().getEvents().enqueue(new Callback<List<EventItem>>() {
            @Override
            public void onResponse(Call<List<EventItem>> call, Response<List<EventItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events.clear();
                    events.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EventListActivity.this, "Yükleme hatası: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventItem>> call, Throwable t) {
                Toast.makeText(EventListActivity.this, "Bağlantı hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
