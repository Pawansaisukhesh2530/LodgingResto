package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.AiRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRequestHistoryRepository extends JpaRepository<AiRequestHistory, Long> {
}
