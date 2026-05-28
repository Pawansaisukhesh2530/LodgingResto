package com.example.lodgingresto.controller;

import com.example.lodgingresto.service.ApiUsageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

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
        
        long avgTime = apiUsageService.averageResponseTimeMs();
        model.addAttribute("averageResponseTime", avgTime);

        List<Object[]> telemetry = apiUsageService.dailyUsageTelemetry();
        String apiChartLabels = telemetry.stream().map(o -> o[0].toString()).collect(Collectors.joining("|"));
        String apiChartValues = telemetry.stream().map(o -> o[1].toString()).collect(Collectors.joining("|"));
        
        model.addAttribute("apiChartLabels", apiChartLabels);
        model.addAttribute("apiChartValues", apiChartValues);

        return "api-dashboard";
    }
}

