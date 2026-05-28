package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.ApiResponse;
import com.example.lodgingresto.model.Invoice;
import com.example.lodgingresto.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class ApiBillingController {

    private final BillingService billingService;

    public ApiBillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Invoice>>> listAll() {
        List<Invoice> data = billingService.getAllInvoices();
        return ResponseEntity.ok(new ApiResponse<>("success", "Invoices fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Invoice>> getById(@PathVariable Long id) {
        Invoice data = billingService.getInvoiceById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Invoice not found with id " + id));
        return ResponseEntity.ok(new ApiResponse<>("success", "Invoice found", data));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> getRevenue() {
        return ResponseEntity.ok(new ApiResponse<>("success", "Total revenue", billingService.getRevenue()));
    }
}
