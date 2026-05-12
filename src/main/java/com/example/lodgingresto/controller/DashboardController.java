package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.Room;
import com.example.lodgingresto.model.RoomStatus;
import com.example.lodgingresto.model.RoomType;
import com.example.lodgingresto.service.RoomService;
import jakarta.validation.Valid;
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



@Controller
@RequestMapping
public class DashboardController {

    private final RoomService roomService;

    public DashboardController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        addCommonAttributes(model);
        model.addAttribute("totalRooms", roomService.countAllRooms());
        model.addAttribute("availableRooms", roomService.countAvailableRooms());
        model.addAttribute("occupiedRooms", roomService.countOccupiedRooms());
        model.addAttribute("revenue", roomService.calculateRevenue());
        return "dashboard";
    }

    @GetMapping("/rooms")
    public String rooms(@RequestParam(required = false) String search,
                        @RequestParam(required = false) RoomType roomType,
                        @RequestParam(required = false) RoomStatus status,
                        Model model) {
        addCommonAttributes(model);
        model.addAttribute("rooms", roomService.getAllRooms(search, roomType, status));
        model.addAttribute("search", search);
        model.addAttribute("selectedRoomType", roomType);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("totalRooms", roomService.countAllRooms());
        model.addAttribute("availableRooms", roomService.countAvailableRooms());
        model.addAttribute("occupiedRooms", roomService.countOccupiedRooms());
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
            bindingResult.rejectValue("roomNumber", "roomNumber.exists", ex.getMessage());
            addCommonAttributes(model);
            return "add-room";
        }
    }

    @GetMapping("/rooms/{id}/edit")
    public String editRoomForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
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
    public String updateRoom(@PathVariable Long id,
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
            bindingResult.rejectValue("roomNumber", "roomNumber.exists", ex.getMessage());
            addCommonAttributes(model);
            return "edit-room";
        }
    }

    @PostMapping("/rooms/{id}/delete")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Room room = roomService.getRoomById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found"));


            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/rooms";
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("roomStatuses", RoomStatus.values());
    }
}

