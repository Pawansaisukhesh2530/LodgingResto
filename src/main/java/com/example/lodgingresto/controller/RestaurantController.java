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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String search,
                        @RequestParam(required = false) Long categoryId,
                        Model model) {
        model.addAttribute("categories", restaurantService.getAllCategories());
        model.addAttribute("menuItems", restaurantService.searchMenuItems(search, categoryId));
        model.addAttribute("orders", restaurantService.getAllOrders());
        model.addAttribute("newOrder", new RestaurantOrder());
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("category", new MenuCategory());
        model.addAttribute("search", search);
        model.addAttribute("categoryId", categoryId);
        return "restaurant";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return restaurantService.getMenuItemById(id)
                .map(item -> {
                    model.addAttribute("menuItem", item);
                    model.addAttribute("categories", restaurantService.getAllCategories());
                    model.addAttribute("menuItems", restaurantService.getAllMenuItems());
                    model.addAttribute("orders", restaurantService.getAllOrders());
                    model.addAttribute("newOrder", new RestaurantOrder());
                    model.addAttribute("category", new MenuCategory());
                    return "edit-menu-item";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "Menu item not found.");
                    return "redirect:/restaurant";
                });
    }

    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute MenuCategory category, BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("categories", restaurantService.getAllCategories());
            model.addAttribute("menuItems", restaurantService.getAllMenuItems());
            model.addAttribute("orders", restaurantService.getAllOrders());
            model.addAttribute("newOrder", new RestaurantOrder());
            model.addAttribute("menuItem", new MenuItem());
            return "restaurant";
        }
        restaurantService.createCategory(category);
        ra.addFlashAttribute("successMessage", "Category added");
        return "redirect:/restaurant";
    }

    @PostMapping("/items")
    public String createItem(@Valid @ModelAttribute("menuItem") MenuItem item,
                             BindingResult br,
                             @RequestParam Long categoryId,
                             Model model,
                             RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("categories", restaurantService.getAllCategories());
            model.addAttribute("menuItems", restaurantService.getAllMenuItems());
            model.addAttribute("orders", restaurantService.getAllOrders());
            model.addAttribute("newOrder", new RestaurantOrder());
            model.addAttribute("category", new MenuCategory());
            return "restaurant";
        }
        restaurantService.createMenuItem(item, categoryId);
        ra.addFlashAttribute("successMessage", "Menu item added");
        return "redirect:/restaurant";
    }

    @PutMapping("/items/{id}")
    public String updateItem(@PathVariable Long id,
                             @Valid @ModelAttribute("menuItem") MenuItem item,
                             BindingResult br,
                             @RequestParam Long categoryId,
                             Model model,
                             RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("categories", restaurantService.getAllCategories());
            model.addAttribute("menuItems", restaurantService.getAllMenuItems());
            model.addAttribute("orders", restaurantService.getAllOrders());
            model.addAttribute("newOrder", new RestaurantOrder());
            model.addAttribute("category", new MenuCategory());
            return "edit-menu-item";
        }
        restaurantService.updateMenuItem(id, item, categoryId);
        ra.addFlashAttribute("successMessage", "Menu item updated");
        return "redirect:/restaurant";
    }

    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable Long id, RedirectAttributes ra) {
        restaurantService.deleteMenuItem(id);
        ra.addFlashAttribute("successMessage", "Menu item deleted");
        return "redirect:/restaurant";
    }

    @PostMapping("/orders")
    public String createOrder(@ModelAttribute RestaurantOrder order, RedirectAttributes ra) {
        restaurantService.createOrder(order);
        ra.addFlashAttribute("successMessage", "Order placed");
        return "redirect:/restaurant";
    }
}

