package com.tbl324.event.repository;

import com.tbl324.event.domain.Event;
import com.tbl324.event.domain.EventStatus;
import com.tbl324.event.domain.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventRepositoryTest {

    static {
        System.setProperty("DOCKER_HOST", "tcp://localhost:2375");
        System.setProperty("DOCKER_API_VERSION", "1.41");
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EventJdbcRepository eventRepository;

    @Autowired
    private VenueJdbcRepository venueRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long savedVenueId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE seats, events, venues RESTART IDENTITY CASCADE");
        Venue venue = Venue.builder()
                .name("Test Salonu")
                .address("Test Adres")
                .capacity(100)
                .build();
        savedVenueId = venueRepository.save(venue).getId();
    }

    @Test
    void save_thenFindById_returnsPersistedEvent() {
        Event event = buildEvent("Spring Boot Workshop");
        Event saved = eventRepository.save(event);

        assertThat(saved.getId()).isNotNull();

        Optional<Event> found = eventRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Spring Boot Workshop");
        assertThat(found.get().getStatus()).isEqualTo(EventStatus.ACTIVE);
    }

    @Test
    void findAll_withPagination_returnsCorrectPage() {
        for (int i = 1; i <= 5; i++) {
            eventRepository.save(buildEvent("Etkinlik " + i));
        }

        List<Event> page1 = eventRepository.findList(0, 3);
        List<Event> page2 = eventRepository.findList(3, 3);

        assertThat(page1).hasSize(3);
        assertThat(page2).hasSize(2);
    }

    @Test
    void count_returnsTotal() {
        eventRepository.save(buildEvent("E1"));
        eventRepository.save(buildEvent("E2"));

        assertThat(eventRepository.count()).isEqualTo(2);
    }

    @Test
    void delete_removesEvent() {
        Event saved = eventRepository.save(buildEvent("Silinecek Etkinlik"));
        eventRepository.delete(saved.getId());

        assertThat(eventRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        Optional<Event> result = eventRepository.findById(999_999L);
        assertThat(result).isEmpty();
    }

    @Test
    void sqlInjection_inTitle_isHarmless() {
        String maliciousTitle = "'; DROP TABLE events; --";
        Event saved = eventRepository.save(buildEvent(maliciousTitle));

        Optional<Event> found = eventRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo(maliciousTitle);

        // Tablo hâlâ var, injection çalışmadı
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM events", Integer.class);
        assertThat(count).isGreaterThan(0);
    }

    @Test
    void connectionLeak_after1000Calls_hikariReturnsToBaseline() {
        int baseline = getActiveConnections();
        for (int i = 0; i < 1000; i++) {
            eventRepository.count();
        }
        int after = getActiveConnections();
        assertThat(after).isLessThanOrEqualTo(baseline + 2);
    }

    private int getActiveConnections() {
        Integer active = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM pg_stat_activity WHERE state = 'active'", Integer.class);
        return active != null ? active : 0;
    }

    private Event buildEvent(String title) {
        return Event.builder()
                .title(title)
                .description("Açıklama")
                .venueId(savedVenueId)
                .startTime(LocalDateTime.now().plusDays(7))
                .endTime(LocalDateTime.now().plusDays(7).plusHours(3))
                .totalSeats(100)
                .availableSeats(100)
                .status(EventStatus.ACTIVE)
                .build();
    }
}
