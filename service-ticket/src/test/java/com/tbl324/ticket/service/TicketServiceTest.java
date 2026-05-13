package com.tbl324.ticket.service;

import com.tbl324.ticket.client.EventServiceClient;
import com.tbl324.ticket.client.NotificationServiceClient;
import com.tbl324.ticket.domain.TicketStatus;
import com.tbl324.ticket.dto.ReserveRequest;
import com.tbl324.ticket.dto.TicketDTO;
import com.tbl324.ticket.payment.FailingPaymentStrategy;
import com.tbl324.ticket.payment.MockPaymentStrategy;
import com.tbl324.ticket.repository.TicketJdbcRepository;
import com.tbl324.shared.exception.ConflictException;
import com.tbl324.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock TicketJdbcRepository ticketRepository;
    @Mock SeatLockService seatLockService;
    @Mock EventServiceClient eventServiceClient;
    @Mock NotificationServiceClient notificationServiceClient;

    @InjectMocks TicketService ticketService;

    @Test
    void reserve_happyPath_returnsPendingTicket() {
        when(eventServiceClient.eventExists(1L)).thenReturn(true);
        when(seatLockService.tryLock(anyLong(), anyLong(), anyString(), anyLong())).thenReturn(true);
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReserveRequest req = new ReserveRequest(1L, 5L, 10L);
        TicketDTO result = ticketService.reserve(req);

        assertThat(result.status()).isEqualTo(TicketStatus.PENDING);
        assertThat(result.eventId()).isEqualTo(1L);
        assertThat(result.seatId()).isEqualTo(5L);
    }

    @Test
    void reserve_eventNotFound_throwsNotFoundException() {
        when(eventServiceClient.eventExists(99L)).thenReturn(false);

        assertThatThrownBy(() -> ticketService.reserve(new ReserveRequest(99L, 1L, 1L)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void reserve_seatAlreadyLocked_throwsConflictException() {
        when(eventServiceClient.eventExists(1L)).thenReturn(true);
        when(seatLockService.tryLock(anyLong(), anyLong(), anyString(), anyLong())).thenReturn(false);

        assertThatThrownBy(() -> ticketService.reserve(new ReserveRequest(1L, 5L, 10L)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void confirm_withMockPayment_returnsConfirmedTicket() {
        TicketDTO pending = new TicketDTO(1L, 1L, 5L, 10L, TicketStatus.PENDING);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(pending));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TicketDTO result = ticketService.confirm(1L, new MockPaymentStrategy());

        assertThat(result.status()).isEqualTo(TicketStatus.CONFIRMED);
    }

    @Test
    void confirm_withFailingPayment_throwsConflictException() {
        TicketDTO pending = new TicketDTO(1L, 1L, 5L, 10L, TicketStatus.PENDING);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> ticketService.confirm(1L, new FailingPaymentStrategy()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void confirm_ticketNotFound_throwsNotFoundException() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.confirm(999L, new MockPaymentStrategy()))
                .isInstanceOf(NotFoundException.class);
    }
}
