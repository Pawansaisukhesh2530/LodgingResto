package com.example.lodgingresto.controller;

import com.example.lodgingresto.model.InventoryItem;
import com.example.lodgingresto.model.Supplier;
import com.example.lodgingresto.service.InventoryService;
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
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("items", inventoryService.getAllItems());
        model.addAttribute("lowStock", inventoryService.getLowStockItems(5));
        model.addAttribute("suppliers", inventoryService.getAllSuppliers());
        return "inventory";
    }

    @PostMapping("/items")
    public String addItem(@Valid @ModelAttribute InventoryItem item, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "inventory";
        inventoryService.addItem(item);
        ra.addFlashAttribute("successMessage", "Inventory item added");
        return "redirect:/inventory";
    }

    @PostMapping("/suppliers")
    public String addSupplier(@Valid @ModelAttribute Supplier supplier, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "inventory";
        inventoryService.addSupplier(supplier);
        ra.addFlashAttribute("successMessage", "Supplier added");
        return "redirect:/inventory";
    }
}

