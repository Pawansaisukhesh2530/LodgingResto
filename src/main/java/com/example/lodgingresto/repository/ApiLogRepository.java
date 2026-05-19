package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {

    @Query("select a.endpoint, count(a) from ApiLog a group by a.endpoint order by count(a) desc")
    List<Object[]> countByEndpoint();
}

