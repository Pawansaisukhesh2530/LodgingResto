package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByOrderByCreatedAtDesc();
    long countByReadFalse();
}
