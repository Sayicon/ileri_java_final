package com.tbl324.ticket.controller;

import com.tbl324.ticket.dto.ConfirmRequest;
import com.tbl324.ticket.dto.ReserveRequest;
import com.tbl324.ticket.dto.TicketDTO;
import com.tbl324.ticket.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDTO reserve(@Valid @RequestBody ReserveRequest req) {
        return ticketService.reserve(req);
    }

    @PostMapping("/{id}/confirm")
    public TicketDTO confirm(@PathVariable Long id, @Valid @RequestBody ConfirmRequest req) {
        return ticketService.confirm(id, req.getPaymentType());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        ticketService.cancel(id);
    }
}
