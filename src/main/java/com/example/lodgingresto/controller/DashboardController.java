package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.*;
import com.example.lodgingresto.service.BillingService;
import com.example.lodgingresto.service.EmployeeService;
import com.example.lodgingresto.service.GuestService;
import com.example.lodgingresto.service.InventoryService;
import com.example.lodgingresto.service.ReservationService;
import com.example.lodgingresto.service.RestaurantService;
import com.example.lodgingresto.service.RoomService;
import com.example.lodgingresto.service.ApiUsageService;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Comparator;

@Controller
@RequestMapping
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final RoomService roomService;
    private final GuestService guestService;
    private final ReservationService reservationService;
    private final EmployeeService employeeService;
    private final InventoryService inventoryService;
    private final RestaurantService restaurantService;
    private final BillingService billingService;
    private final ApiUsageService apiUsageService;

    public DashboardController(RoomService roomService,
                               GuestService guestService,
                               ReservationService reservationService,
                               EmployeeService employeeService,
                               InventoryService inventoryService,
                               RestaurantService restaurantService,
                               BillingService billingService,
                               ApiUsageService apiUsageService) {
        this.roomService = roomService;
        this.guestService = guestService;
        this.reservationService = reservationService;
        this.employeeService = employeeService;
        this.inventoryService = inventoryService;
        this.restaurantService = restaurantService;
        this.billingService = billingService;
        this.apiUsageService = apiUsageService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        addCommonAttributes(model);

        long totalRoomsCount = roomService.countAllRooms();
        long availableRoomsCount = roomService.countAvailableRooms();
        long occupiedRoomsCount = roomService.countOccupiedRooms();
        long reservedRoomsCount = roomService.countReservedRooms();
        long maintenanceRoomsCount = roomService.countMaintenanceRooms();

        long totalGuestsCount = guestService.getAllGuests().size();
        BigDecimal revenueAmount = billingService.getRevenue();
        long apiRequestsCount = apiUsageService.totalRequests();
        BigDecimal monthlyRevenue = billingService.getMonthlyRevenue();
        long inventoryAlertsCount = inventoryService.getLowStockItems(5).size();

        List<Reservation> allReservations = reservationService.getAllReservations();
        long totalReservationsCount = allReservations.size();
        long pendingPaymentsCount = allReservations.stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getPaymentStatus()))
                .count();
        long paidCount = allReservations.stream()
                .filter(r -> "PAID".equalsIgnoreCase(r.getPaymentStatus()))
                .count();
        long otherResCount = allReservations.size() - paidCount - pendingPaymentsCount;

        List<RestaurantOrder> allOrders = restaurantService.getAllOrders();
        long restaurantOrdersCount = allOrders.size();

        List<Invoice> allInvoices = billingService.getAllInvoices();

        List<InventoryItem> allItems = inventoryService.getAllItems();
        allItems.sort(Comparator.comparingInt(InventoryItem::getQuantity));

        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("totalRooms", totalRoomsCount);
        model.addAttribute("availableRooms", availableRoomsCount);
        model.addAttribute("occupiedRooms", occupiedRoomsCount);
        model.addAttribute("reservedRooms", reservedRoomsCount);
        model.addAttribute("maintenanceRooms", maintenanceRoomsCount);
        model.addAttribute("totalGuests", totalGuestsCount);
        model.addAttribute("totalReservations", totalReservationsCount);
        model.addAttribute("restaurantOrders", restaurantOrdersCount);
        model.addAttribute("revenue", revenueAmount);
        model.addAttribute("apiRequests", apiRequestsCount);
        model.addAttribute("pendingPayments", pendingPaymentsCount);
        model.addAttribute("inventoryAlerts", inventoryAlertsCount);

        List<Reservation> recentReservations = allReservations.stream()
                .sorted((a, b) -> (b.getId() != null && a.getId() != null) ? b.getId().compareTo(a.getId()) : 0)
                .limit(5).toList();
        List<RestaurantOrder> recentOrders = allOrders.stream()
                .sorted((a, b) -> (b.getId() != null && a.getId() != null) ? b.getId().compareTo(a.getId()) : 0)
                .limit(5).toList();
        List<Invoice> recentInvoices = allInvoices.stream()
                .sorted((a, b) -> (b.getId() != null && a.getId() != null) ? b.getId().compareTo(a.getId()) : 0)
                .limit(5).toList();
        List<ApiLog> recentApiLogs = apiUsageService.recentCalls(5);

        model.addAttribute("recentReservations", recentReservations);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("recentInvoices", recentInvoices);
        model.addAttribute("recentApiLogs", recentApiLogs);

        allInvoices.sort(Comparator.comparing(Invoice::getCreatedAt));
        Map<String, BigDecimal> revByMonth = new LinkedHashMap<>();
        java.time.format.DateTimeFormatter monthFormatter = java.time.format.DateTimeFormatter.ofPattern("MMM yy");
        for (Invoice inv : allInvoices) {
            String mName = inv.getCreatedAt().format(monthFormatter);
            revByMonth.put(mName, revByMonth.getOrDefault(mName, BigDecimal.ZERO).add(inv.getTotalAmount()));
        }
        model.addAttribute("revenueChartLabels", String.join("|", revByMonth.keySet()));
        model.addAttribute("revenueChartValues", revByMonth.values().stream().map(BigDecimal::toString).collect(Collectors.joining("|")));

        model.addAttribute("roomChartLabels", "Available|Occupied|Reserved|Maintenance");
        model.addAttribute("roomChartValues", availableRoomsCount + "|" + occupiedRoomsCount + "|" + reservedRoomsCount + "|" + maintenanceRoomsCount);

        String resLabels = "Paid|Pending" + (otherResCount > 0 ? "|Other" : "");
        String resValues = paidCount + "|" + pendingPaymentsCount + (otherResCount > 0 ? "|" + otherResCount : "");
        model.addAttribute("resChartLabels", resLabels);
        model.addAttribute("resChartValues", resValues);

        allOrders.sort(Comparator.comparing(RestaurantOrder::getCreatedAt));
        Map<String, BigDecimal> salesByDay = new LinkedHashMap<>();
        java.time.format.DateTimeFormatter dayFormatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM");
        for (RestaurantOrder ord : allOrders) {
            String dName = ord.getCreatedAt().format(dayFormatter);
            salesByDay.put(dName, salesByDay.getOrDefault(dName, BigDecimal.ZERO).add(ord.getTotal()));
        }
        List<String> rDays = salesByDay.keySet().stream().toList();
        List<BigDecimal> rSales = salesByDay.values().stream().toList();
        int limitDays = Math.min(7, rDays.size());
        String restLabels = "";
        String restValues = "";
        if (limitDays > 0) {
            int startIdx = rDays.size() - limitDays;
            restLabels = String.join("|", rDays.subList(startIdx, rDays.size()));
            restValues = rSales.subList(startIdx, rSales.size()).stream().map(BigDecimal::toString).collect(Collectors.joining("|"));
        }
        model.addAttribute("restaurantChartLabels", restLabels);
        model.addAttribute("restaurantChartValues", restValues);

        List<Object[]> apiTelemetry = apiUsageService.dailyUsageTelemetry();
        model.addAttribute("apiChartLabels", apiTelemetry.stream().map(o -> o[0].toString()).collect(Collectors.joining("|")));
        model.addAttribute("apiChartValues", apiTelemetry.stream().map(o -> o[1].toString()).collect(Collectors.joining("|")));

        List<InventoryItem> top5Inv = allItems.stream().limit(6).toList();
        model.addAttribute("inventoryChartLabels", top5Inv.stream().map(InventoryItem::getName).collect(Collectors.joining("|")));
        model.addAttribute("inventoryChartValues", top5Inv.stream().map(i -> String.valueOf(i.getQuantity())).collect(Collectors.joining("|")));

        return "dashboard";
    }

    @GetMapping("/rooms")
    public String rooms(@RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "roomType", required = false) RoomType roomType,
                        @RequestParam(name = "status", required = false) RoomStatus status,
                        Model model) {
        addCommonAttributes(model);
        model.addAttribute("rooms", roomService.getAllRooms(search, roomType, status));
        model.addAttribute("search", search);
        model.addAttribute("selectedRoomType", roomType);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("totalRooms", roomService.countAllRooms());
        model.addAttribute("availableRooms", roomService.countAvailableRooms());
        model.addAttribute("occupiedRooms", roomService.countOccupiedRooms());
        model.addAttribute("reservedRooms", roomService.countReservedRooms());
        model.addAttribute("maintenanceRooms", roomService.countMaintenanceRooms());
        model.addAttribute("revenue", roomService.calculateRevenue());
        return "rooms";
    }

    @GetMapping("/rooms/new")
    public String addRoomForm(Model model) {
        addCommonAttributes(model);
        model.addAttribute("room", new Room());
        return "add-room";
    }

    @PostMapping("/rooms")
    public String createRoom(@Valid @ModelAttribute("room") Room room,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            return "add-room";
        }
        try {
            roomService.createRoom(room);
            redirectAttributes.addFlashAttribute("successMessage", "Room created successfully.");
            return "redirect:/rooms";
        } catch (IllegalArgumentException ex) {
            logger.error("Illegal argument error while creating room: {}", ex.getMessage());
            bindingResult.rejectValue("roomNumber", "roomNumber.exists", ex.getMessage());
            addCommonAttributes(model);
            return "add-room";
        } catch (Exception ex) {
            logger.error("Unexpected error while creating room", ex);
            bindingResult.reject("global", "An error occurred while creating the room: " + ex.getMessage());
            addCommonAttributes(model);
            return "add-room";
        }
    }

    @GetMapping("/rooms/{id}/edit")
    public String editRoomForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return roomService.getRoomById(id)
                .map(room -> {
                    addCommonAttributes(model);
                    model.addAttribute("room", room);
                    return "edit-room";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Room not found.");
                    return "redirect:/rooms";
                });
    }

    @PutMapping("/rooms/{id}")
    public String updateRoom(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("room") Room room,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            return "edit-room";
        }
        try {
            roomService.updateRoom(id, room);
            redirectAttributes.addFlashAttribute("successMessage", "Room updated successfully.");
            return "redirect:/rooms";
        } catch (IllegalArgumentException ex) {
            logger.error("Illegal argument error while updating room with ID {}: {}", id, ex.getMessage());
            bindingResult.rejectValue("roomNumber", "roomNumber.exists", ex.getMessage());
            addCommonAttributes(model);
            return "edit-room";
        } catch (Exception ex) {
            logger.error("Unexpected error while updating room with ID {}", id, ex);
            bindingResult.reject("global", "An error occurred while updating the room: " + ex.getMessage());
            addCommonAttributes(model);
            return "edit-room";
        }
    }

    @PostMapping("/rooms/{id}/delete")
    public String deleteRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Attempting to delete room with ID: {}", id);
            
            // Check if room exists
            if (roomService.getRoomById(id).isEmpty()) {
                logger.warn("Room with ID {} not found for deletion", id);
                redirectAttributes.addFlashAttribute("errorMessage", "Room not found.");
                return "redirect:/rooms";
            }

            // Delete the room
            roomService.deleteRoom(id);
            logger.info("Room with ID {} deleted successfully", id);
            redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully.");
            
        } catch (IllegalArgumentException ex) {
            logger.error("Illegal argument error while deleting room with ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error while deleting room with ID {}", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the room: " + ex.getMessage());
        }
        
        return "redirect:/rooms";
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("roomStatuses", RoomStatus.values());
    }
}

