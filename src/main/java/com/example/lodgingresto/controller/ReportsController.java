package com.example.lodgingresto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    @GetMapping
    public String index(Model model) {
        // Example dataset; real data should be provided by ReportService
        model.addAttribute("revenueData", List.of(1200, 1400, 1100, 1800, 1500, 2000));
        model.addAttribute("labels", List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun"));
        return "reports";
    }
}


