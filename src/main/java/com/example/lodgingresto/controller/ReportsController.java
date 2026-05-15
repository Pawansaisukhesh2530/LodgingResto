package com.example.lodgingresto.controller;

import com.example.lodgingresto.service.BillingService;
import com.example.lodgingresto.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
        model.addAttribute("revenueData", List.of(
                roomService.calculateRevenue().intValue(),
                billingService.getRevenue().intValue(),
                billingService.getTaxAmount().intValue(),
                billingService.getAllInvoices().size()));
        model.addAttribute("labels", List.of("Rooms", "Revenue", "Tax", "Invoices"));
        return "reports";
    }
}


