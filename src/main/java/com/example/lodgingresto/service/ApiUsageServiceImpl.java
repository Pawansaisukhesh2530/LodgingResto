package com.example.lodgingresto.service;

import com.example.lodgingresto.model.ApiLog;
import com.example.lodgingresto.repository.ApiLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public long successfulRequests() { return repo.findAll().stream().filter(l -> l.getResponseStatus() != null && l.getResponseStatus() >= 200 && l.getResponseStatus() < 400).count(); }

    @Override
    @Transactional(readOnly = true)
    public long failedRequests() { return repo.findAll().stream().filter(l -> l.getResponseStatus() == null || l.getResponseStatus() >= 400).count(); }

    @Override
    @Transactional(readOnly = true)
    public List<ApiLog> recentCalls(int limit) {
        List<ApiLog> all = repo.findAll();
        all.sort((a,b)-> b.getRequestTime().compareTo(a.getRequestTime()));
        return all.stream().limit(limit).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> mostUsedEndpoints() {
        return repo.countByEndpoint();
    }
}

