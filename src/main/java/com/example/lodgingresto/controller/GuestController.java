package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.Guest;
import com.example.lodgingresto.service.GuestService;
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
@RequestMapping("/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public String listGuests(@RequestParam(name = "search", required = false) String search, Model model) {
        model.addAttribute("guests", guestService.searchGuests(search));
        model.addAttribute("search", search);
        model.addAttribute("guest", new Guest());
        return "guests";
    }

    @GetMapping("/new")
    public String newGuest(Model model) {
        model.addAttribute("guest", new Guest());
        model.addAttribute("guests", guestService.getAllGuests());
        return "guests";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        return guestService.getGuestById(id)
                .map(guest -> {
                    model.addAttribute("guest", guest);
                    model.addAttribute("guests", guestService.getAllGuests());
                    return "edit-guest";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "Guest not found.");
                    return "redirect:/guests";
                });
    }

    @PostMapping
    public String createGuest(@Valid @ModelAttribute("guest") Guest guest,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("guests", guestService.getAllGuests());
            return "guests";
        }
        guestService.createGuest(guest);
        redirectAttributes.addFlashAttribute("successMessage", "Guest created successfully.");
        return "redirect:/guests";
    }

    @PutMapping("/{id}")
    public String updateGuest(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("guest") Guest guest,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("guests", guestService.getAllGuests());
            return "edit-guest";
        }
        guestService.updateGuest(id, guest);
        redirectAttributes.addFlashAttribute("successMessage", "Guest updated successfully.");
        return "redirect:/guests";
    }

    @PostMapping("/{id}/delete")
    public String deleteGuest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        guestService.deleteGuest(id);
        redirectAttributes.addFlashAttribute("successMessage", "Guest deleted successfully.");
        return "redirect:/guests";
    }
}

