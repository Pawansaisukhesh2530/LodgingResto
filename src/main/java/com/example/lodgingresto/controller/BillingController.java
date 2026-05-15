package com.example.lodgingresto.controller;

import com.example.lodgingresto.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final RoomService roomService;

    public BillingController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public String index(Model model) {
        BigDecimal revenue = roomService.calculateRevenue();
        BigDecimal tax = revenue.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = revenue.add(tax).setScale(2, RoundingMode.HALF_UP);
        model.addAttribute("revenue", revenue);
        model.addAttribute("tax", tax);
        model.addAttribute("total", total);
        model.addAttribute("methods", new String[]{"Cash", "Card", "UPI"});
        return "billing";
    }
}

