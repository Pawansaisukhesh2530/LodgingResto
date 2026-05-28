package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Invoice;
import com.example.lodgingresto.model.InvoiceType;
import com.example.lodgingresto.model.PaymentMethod;
import com.example.lodgingresto.model.Reservation;
import com.example.lodgingresto.model.RestaurantOrder;
import com.example.lodgingresto.repository.InvoiceRepository;
import com.example.lodgingresto.repository.ReservationRepository;
import com.example.lodgingresto.repository.RestaurantOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;

    public BillingServiceImpl(InvoiceRepository invoiceRepository,
                              ReservationRepository reservationRepository,
                              RestaurantOrderRepository restaurantOrderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.reservationRepository = reservationRepository;
        this.restaurantOrderRepository = restaurantOrderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public Invoice createInvoice(Invoice invoice, Long reservationId, Long orderId) {
        BigDecimal subtotal = BigDecimal.ZERO;
        Reservation reservation = null;
        RestaurantOrder restaurantOrder = null;

        if (reservationId != null) {
            reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            long nights = Math.max(1, ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate()));
            BigDecimal roomPrice = reservation.getRoom().getPrice();
            subtotal = subtotal.add(roomPrice.multiply(BigDecimal.valueOf(nights)));
        }

        if (orderId != null) {
            restaurantOrder = restaurantOrderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            subtotal = subtotal.add(restaurantOrder.getTotal());
        }

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("At least one reservation or order must be selected");
        }

        invoice.setReservation(reservation);
        invoice.setRestaurantOrder(restaurantOrder);
        invoice.setInvoiceNumber(buildInvoiceNumber());
        invoice.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        invoice.setTaxAmount(invoice.getSubtotal().multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP));
        invoice.setTotalAmount(invoice.getSubtotal().add(invoice.getTaxAmount()).setScale(2, RoundingMode.HALF_UP));
        if (invoice.getInvoiceType() == null) {
            invoice.setInvoiceType(orderId != null && reservationId != null ? InvoiceType.COMBINED
                    : reservationId != null ? InvoiceType.ROOM : InvoiceType.RESTAURANT);
        }
        if (invoice.getPaymentMethod() == null) {
            invoice.setPaymentMethod(PaymentMethod.CASH);
        }
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRevenue() {
        return invoiceRepository.findAll().stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getMonthlyRevenue() {
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.YearMonth currentMonth = java.time.YearMonth.from(now);
        return invoiceRepository.findAll().stream()
                .filter(i -> i.getCreatedAt() != null
                        && java.time.YearMonth.from(i.getCreatedAt().toLocalDate()).equals(currentMonth))
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTaxAmount() {
        return invoiceRepository.findAll().stream()
                .map(Invoice::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String buildInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

