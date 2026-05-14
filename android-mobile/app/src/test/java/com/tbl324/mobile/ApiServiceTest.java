package com.tbl324.mobile;

import com.tbl324.mobile.api.ApiService;
import com.tbl324.mobile.model.EventItem;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

import static org.junit.Assert.*;

public class ApiServiceTest {

    private MockWebServer server;
    private ApiService service;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();

        service = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void getEvents_success_returnsEventList() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"id\":1,\"name\":\"Konser\",\"venue\":\"Stadyum\"}]"));

        List<EventItem> events = service.getEvents().execute().body();

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Konser", events.get(0).getName());
    }

    @Test
    public void getEvents_serverError_returnsNullBody() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));

        retrofit2.Response<List<EventItem>> resp = service.getEvents().execute();

        assertFalse(resp.isSuccessful());
        assertEquals(500, resp.code());
    }

    @Test
    public void getEvents_unauthorized_returns401() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401));

        retrofit2.Response<List<EventItem>> resp = service.getEvents().execute();

        assertEquals(401, resp.code());
    }
}
