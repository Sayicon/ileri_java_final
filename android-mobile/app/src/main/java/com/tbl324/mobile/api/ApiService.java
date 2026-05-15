package com.tbl324.mobile.api;

import com.tbl324.mobile.model.TicketItem;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/events")
    Call<EventsResponse> getEvents();

    @GET("api/events/{id}/seats")
    Call<SeatsResponse> getSeats(@Path("id") long eventId);

    @POST("api/auth/login")
    Call<Map<String, String>> login(@Body Map<String, String> credentials);

    @POST("api/tickets/reserve")
    Call<Void> reserve(@Body Map<String, Long> body);

    @GET("api/tickets/my")
    Call<List<TicketItem>> getMyTickets(@Query("userId") long userId);
}
