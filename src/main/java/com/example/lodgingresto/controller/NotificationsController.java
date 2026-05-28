package com.example.lodgingresto.controller;

import com.example.lodgingresto.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
public class NotificationsController {

    private final NotificationService notificationService;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("notifications", notificationService.getAllNotifications());
        return "notifications";
    }
}

