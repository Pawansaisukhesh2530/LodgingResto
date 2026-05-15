package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.Reservation;
import com.example.lodgingresto.service.GuestService;
import com.example.lodgingresto.service.ReservationService;
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
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final GuestService guestService;
    private final RoomService roomService;

    public ReservationController(ReservationService reservationService, GuestService guestService, RoomService roomService) {
        this.reservationService = reservationService;
        this.guestService = guestService;
        this.roomService = roomService;
    }

    @GetMapping
    public String listReservations(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("reservations", reservationService.searchReservations(search));
        model.addAttribute("search", search);
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("guests", guestService.getAllGuests());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "reservations";
    }

    @GetMapping("/new")
    public String newReservation(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("reservations", reservationService.getAllReservations());
        model.addAttribute("guests", guestService.getAllGuests());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "reservations";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return reservationService.getReservationById(id)
                .map(reservation -> {
                    model.addAttribute("reservation", reservation);
                    model.addAttribute("reservations", reservationService.getAllReservations());
                    model.addAttribute("guests", guestService.getAllGuests());
                    model.addAttribute("rooms", roomService.getAllRooms());
                    return "edit-reservation";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "Reservation not found.");
                    return "redirect:/reservations";
                });
    }

    @PostMapping
    public String createReservation(@Valid @ModelAttribute("reservation") Reservation reservation,
                                    BindingResult bindingResult,
                                    @RequestParam Long guestId,
                                    @RequestParam Long roomId,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reservations", reservationService.getAllReservations());
            model.addAttribute("guests", guestService.getAllGuests());
            model.addAttribute("rooms", roomService.getAllRooms());
            return "reservations";
        }
        reservationService.createReservation(reservation, guestId, roomId);
        redirectAttributes.addFlashAttribute("successMessage", "Reservation created successfully.");
        return "redirect:/reservations";
    }

    @PutMapping("/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @Valid @ModelAttribute("reservation") Reservation reservation,
                                    BindingResult bindingResult,
                                    @RequestParam Long guestId,
                                    @RequestParam Long roomId,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reservations", reservationService.getAllReservations());
            model.addAttribute("guests", guestService.getAllGuests());
            model.addAttribute("rooms", roomService.getAllRooms());
            return "edit-reservation";
        }
        reservationService.updateReservation(id, reservation, guestId, roomId);
        redirectAttributes.addFlashAttribute("successMessage", "Reservation updated successfully.");
        return "redirect:/reservations";
    }

    @PostMapping("/{id}/delete")
    public String deleteReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reservationService.deleteReservation(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reservation deleted successfully.");
        return "redirect:/reservations";
    }
}

