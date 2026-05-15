package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BillingService {
    List<Invoice> getAllInvoices();
    Optional<Invoice> getInvoiceById(Long id);
    Invoice createInvoice(Invoice invoice, Long reservationId, Long orderId);
    BigDecimal getRevenue();
    BigDecimal getTaxAmount();
}

