package com.example.lodgingresto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationsController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("notifications", List.of(
                "Booking confirmation for Room 101 sent.",
                "Payment received for reservation #R-204.",
                "Inventory alert: Linen stock below threshold.",
                "Admin review required for room maintenance request."
        ));
        return "notifications";
    }
}

