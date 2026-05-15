package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.MenuCategory;
import com.example.lodgingresto.model.MenuItem;
import com.example.lodgingresto.model.RestaurantOrder;
import com.example.lodgingresto.service.RestaurantService;
import jakarta.validation.Valid;
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
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("categories", restaurantService.getAllCategories());
        model.addAttribute("orders", restaurantService.getAllOrders());
        model.addAttribute("newOrder", new RestaurantOrder());
        return "restaurant";
    }

    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute MenuCategory category, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "restaurant";
        restaurantService.createCategory(category);
        ra.addFlashAttribute("successMessage", "Category added");
        return "redirect:/restaurant";
    }

    @PostMapping("/items")
    public String createItem(@Valid @ModelAttribute MenuItem item, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "restaurant";
        restaurantService.createMenuItem(item);
        ra.addFlashAttribute("successMessage", "Menu item added");
        return "redirect:/restaurant";
    }

    @PostMapping("/orders")
    public String createOrder(@ModelAttribute RestaurantOrder order, RedirectAttributes ra) {
        restaurantService.createOrder(order);
        ra.addFlashAttribute("successMessage", "Order placed");
        return "redirect:/restaurant";
    }
}

