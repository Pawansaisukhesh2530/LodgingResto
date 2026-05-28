package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.Invoice;
import com.example.lodgingresto.service.BillingService;
import com.example.lodgingresto.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final BillingService billingService;
    private final RoomService roomService;

    public ReportsController(BillingService billingService, RoomService roomService) {
        this.billingService = billingService;
        this.roomService = roomService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("roomRevenue", roomService.calculateRevenue());
        model.addAttribute("totalRevenue", billingService.getRevenue());
        model.addAttribute("totalTax", billingService.getTaxAmount());
        model.addAttribute("totalInvoices", billingService.getAllInvoices().size());

        List<Invoice> invoices = billingService.getAllInvoices();
        invoices.sort(Comparator.comparing(Invoice::getCreatedAt));
        Map<String, BigDecimal> revByMonth = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yy");
        for (Invoice inv : invoices) {
            String m = inv.getCreatedAt().format(fmt);
            revByMonth.put(m, revByMonth.getOrDefault(m, BigDecimal.ZERO).add(inv.getTotalAmount()));
        }
        model.addAttribute("revenueChartLabels", String.join("|", revByMonth.keySet()));
        model.addAttribute("revenueChartValues", revByMonth.values().stream().map(BigDecimal::toString).collect(Collectors.joining("|")));
        return "reports";
    }
}


