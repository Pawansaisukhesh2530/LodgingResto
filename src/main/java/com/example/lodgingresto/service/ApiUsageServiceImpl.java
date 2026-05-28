package com.example.lodgingresto.service;

import com.example.lodgingresto.model.ApiLog;
import com.example.lodgingresto.repository.ApiLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApiUsageServiceImpl implements ApiUsageService {

    private final ApiLogRepository repo;

    public ApiUsageServiceImpl(ApiLogRepository repo) { this.repo = repo; }

    @Override
    public ApiLog record(ApiLog log) {
        return repo.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public long totalRequests() { return repo.count(); }

    @Override
    @Transactional(readOnly = true)
    public long successfulRequests() {
        return repo.findAll().stream()
                .filter(l -> l.getResponseStatus() != null && l.getResponseStatus() >= 200 && l.getResponseStatus() < 400)
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public long failedRequests() {
        return repo.findAll().stream()
                .filter(l -> l.getResponseStatus() == null || l.getResponseStatus() >= 400)
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiLog> recentCalls(int limit) {
        return repo.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "requestTime")))
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> mostUsedEndpoints() {
        return repo.countByEndpoint();
    }

    @Override
    @Transactional(readOnly = true)
    public long averageResponseTimeMs() {
        Double avg = repo.findAll().stream()
                .mapToLong(l -> l.getDurationMs() == null ? 0L : l.getDurationMs())
                .average()
                .orElse(0.0);
        return avg.longValue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> dailyUsageTelemetry() {
        ZoneId zone = ZoneId.systemDefault();
        Map<LocalDate, Long> counts = repo.findAll().stream()
                .filter(l -> l.getRequestTime() != null)
                .collect(Collectors.groupingBy(
                        l -> LocalDate.ofInstant(l.getRequestTime(), zone),
                        Collectors.counting()
                ));

        return counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new Object[]{entry.getKey().toString(), entry.getValue()})
                .collect(Collectors.toList());
    }
}

