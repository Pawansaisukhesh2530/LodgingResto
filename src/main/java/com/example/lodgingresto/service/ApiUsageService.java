package com.example.lodgingresto.service;

import com.example.lodgingresto.model.ApiLog;
import java.util.List;

public interface ApiUsageService {
    ApiLog record(ApiLog log);
    long totalRequests();
    long successfulRequests();
    long failedRequests();
    List<ApiLog> recentCalls(int limit);
    List<Object[]> mostUsedEndpoints();
    long averageResponseTimeMs();
    List<Object[]> dailyUsageTelemetry();
}

