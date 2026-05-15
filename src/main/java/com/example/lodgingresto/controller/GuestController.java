package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.Guest;
import com.example.lodgingresto.service.GuestService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/guests")
public class GuestController {

    private static final Logger logger = LoggerFactory.getLogger(GuestController.class);
    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public String listGuests(Model model) {
        model.addAttribute("guests", guestService.getAllGuests());
        return "guests";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("guest", new Guest());
        return "guests";
    }

    @PostMapping
    public String createGuest(@Valid @ModelAttribute Guest guest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "guests";
        }
        guestService.createGuest(guest);
        redirectAttributes.addFlashAttribute("successMessage", "Guest created successfully.");
        return "redirect:/guests";
    }

    @PostMapping("/{id}/delete")
    public String deleteGuest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        guestService.deleteGuest(id);
        redirectAttributes.addFlashAttribute("successMessage", "Guest deleted successfully.");
        return "redirect:/guests";
    }
}

