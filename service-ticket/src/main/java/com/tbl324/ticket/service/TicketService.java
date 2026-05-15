package com.tbl324.ticket.service;

import com.tbl324.ticket.client.EventServiceClient;
import com.tbl324.ticket.client.NotificationServiceClient;
import com.tbl324.ticket.domain.TicketStatus;
import com.tbl324.ticket.dto.ReserveRequest;
import com.tbl324.ticket.dto.TicketDTO;
import com.tbl324.ticket.payment.MockPaymentStrategy;
import com.tbl324.ticket.payment.PaymentStrategy;
import com.tbl324.ticket.payment.WalletPaymentStrategy;
import com.tbl324.ticket.repository.TicketJdbcRepository;
import com.tbl324.shared.exception.ConflictException;
import com.tbl324.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private static final long LOCK_TTL_SECONDS = 600;

    private final TicketJdbcRepository ticketRepository;
    private final SeatLockService seatLockService;
    private final EventServiceClient eventServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    private final Map<String, PaymentStrategy> strategies;

    public TicketService(TicketJdbcRepository ticketRepository,
                         SeatLockService seatLockService,
                         EventServiceClient eventServiceClient,
                         NotificationServiceClient notificationServiceClient) {
        this.ticketRepository          = ticketRepository;
        this.seatLockService           = seatLockService;
        this.eventServiceClient        = eventServiceClient;
        this.notificationServiceClient = notificationServiceClient;
        this.strategies = Map.of(
                "MOCK",        new MockPaymentStrategy(),
                "WALLET",      new WalletPaymentStrategy(),
                "CASH",        new MockPaymentStrategy(),
                "CREDIT_CARD", new MockPaymentStrategy()
        );
    }

    public TicketDTO reserve(ReserveRequest req) {
        if (!eventServiceClient.eventExists(req.getEventId())) {
            throw new NotFoundException("Etkinlik bulunamadı: " + req.getEventId());
        }

        String ownerId = "user-" + req.getUserId();
        boolean locked = seatLockService.tryLock(
                req.getEventId(), req.getSeatId(), ownerId, LOCK_TTL_SECONDS);

        if (!locked) {
            throw new ConflictException("Koltuk zaten rezerve edilmiş: seatId=" + req.getSeatId());
        }

        TicketDTO toSave = new TicketDTO(null, req.getEventId(), req.getSeatId(), req.getUserId(), TicketStatus.PENDING);
        TicketDTO saved = ticketRepository.save(toSave);
        eventServiceClient.updateSeatStatus(req.getSeatId(), "LOCKED");
        return saved;
    }

    // package-private: TicketServiceTest (same package) erişebilir,
    // TicketControllerTest (farklı paket) göremez → mock belirsizliği ortadan kalkar
    TicketDTO confirm(Long ticketId, PaymentStrategy strategy) {
        TicketDTO existing = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Bilet bulunamadı: " + ticketId));

        if (!strategy.process(ticketId)) {
            throw new ConflictException("Ödeme başarısız: ticketId=" + ticketId);
        }

        TicketDTO confirmed = new TicketDTO(existing.id(), existing.eventId(),
                existing.seatId(), existing.userId(), TicketStatus.CONFIRMED);
        TicketDTO saved = ticketRepository.save(confirmed);
        eventServiceClient.updateSeatStatus(saved.seatId(), "SOLD");

        notificationServiceClient.sendAsync(
                "user-" + saved.userId(),
                "Bilet #" + ticketId + " onaylandı.");

        return saved;
    }

    public TicketDTO confirm(Long ticketId, String paymentType) {
        PaymentStrategy strategy = strategies.getOrDefault(paymentType, strategies.get("MOCK"));
        return confirm(ticketId, strategy);
    }

    public List<TicketDTO> getMyTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll();
    }

    public void cancel(Long ticketId) {
        TicketDTO ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Bilet bulunamadı: " + ticketId));
        seatLockService.release(ticket.eventId(), ticket.seatId(), "user-" + ticket.userId());
        ticketRepository.save(new TicketDTO(ticket.id(), ticket.eventId(),
                ticket.seatId(), ticket.userId(), TicketStatus.CANCELLED));
        eventServiceClient.updateSeatStatus(ticket.seatId(), "AVAILABLE");
    }

    @Scheduled(fixedRate = 60_000)
    public void processExpiredTickets() {
        // 1. Süresi dolan PENDING biletler → EXPIRED + koltuğu serbest bırak
        List<TicketDTO> expiredPending = ticketRepository.findExpiredPending();
        if (!expiredPending.isEmpty()) {
            ticketRepository.deleteExpired();
            expiredPending.forEach(t -> eventServiceClient.updateSeatStatus(t.seatId(), "AVAILABLE"));
            log.info("Süresi dolan {} PENDING bilet EXPIRED yapıldı", expiredPending.size());
        }
        // 2. Etkinliği bitmiş biletler (PENDING + CONFIRMED) → EXPIRED
        List<Long> endedIds = eventServiceClient.getEndedEventIds();
        if (!endedIds.isEmpty()) {
            ticketRepository.expireByEventIds(endedIds);
            log.info("Biten {} etkinliğin biletleri EXPIRED yapıldı", endedIds.size());
        }
    }
}
