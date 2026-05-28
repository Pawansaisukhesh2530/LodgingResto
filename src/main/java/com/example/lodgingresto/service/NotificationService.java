package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications();
    long getUnreadCount();
    Notification createNotification(String message, String category);
    void markAsRead(Long id);
}
