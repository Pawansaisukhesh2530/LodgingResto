package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.Invoice;
import com.example.lodgingresto.model.InvoiceType;
import com.example.lodgingresto.model.PaymentMethod;
import com.example.lodgingresto.service.BillingService;
import com.example.lodgingresto.service.ReservationService;
import com.example.lodgingresto.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;
    private final ReservationService reservationService;
    private final RestaurantService restaurantService;

    public BillingController(BillingService billingService, ReservationService reservationService, RestaurantService restaurantService) {
        this.billingService = billingService;
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String index(Model model) {
        BigDecimal revenue = billingService.getRevenue();
        BigDecimal tax = billingService.getTaxAmount();
        BigDecimal total = revenue.add(tax).setScale(2, RoundingMode.HALF_UP);
        model.addAttribute("invoices", billingService.getAllInvoices());
        model.addAttribute("reservations", reservationService.getAllReservations());
        model.addAttribute("orders", restaurantService.getAllOrders());
        model.addAttribute("revenue", revenue);
        model.addAttribute("tax", tax);
        model.addAttribute("total", total);
        model.addAttribute("invoice", new Invoice());
        model.addAttribute("invoiceTypes", InvoiceType.values());
        model.addAttribute("methods", PaymentMethod.values());
        return "billing";
    }

    @PostMapping
    public String createInvoice(@ModelAttribute("invoice") Invoice invoice,
                                @RequestParam(required = false) Long reservationId,
                                @RequestParam(required = false) Long orderId,
                                RedirectAttributes ra) {
        billingService.createInvoice(invoice, reservationId, orderId);
        ra.addFlashAttribute("successMessage", "Invoice generated successfully.");
        return "redirect:/billing";
    }

    @GetMapping("/{id}")
    public String printableInvoice(@PathVariable Long id, Model model) {
        return billingService.getInvoiceById(id)
                .map(invoice -> {
                    model.addAttribute("invoice", invoice);
                    return "billing";
                })
                .orElse("redirect:/billing");
    }
}

