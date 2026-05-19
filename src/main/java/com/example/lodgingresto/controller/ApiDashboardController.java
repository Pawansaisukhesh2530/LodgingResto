package com.example.lodgingresto.controller;

import com.example.lodgingresto.service.ApiUsageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiDashboardController {

    private final ApiUsageService apiUsageService;

    public ApiDashboardController(ApiUsageService apiUsageService) {
        this.apiUsageService = apiUsageService;
    }

    @GetMapping("/api-dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalRequests", apiUsageService.totalRequests());
        model.addAttribute("successfulRequests", apiUsageService.successfulRequests());
        model.addAttribute("failedRequests", apiUsageService.failedRequests());
        model.addAttribute("mostUsed", apiUsageService.mostUsedEndpoints());
        model.addAttribute("recentCalls", apiUsageService.recentCalls(10));
        return "api-dashboard";
    }
}

