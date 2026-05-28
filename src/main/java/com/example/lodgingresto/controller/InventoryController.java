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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String index(@RequestParam(name = "search", required = false) String search, Model model) {
        model.addAttribute("items", inventoryService.searchItems(search));
        model.addAttribute("lowStock", inventoryService.getLowStockItems(5));
        model.addAttribute("suppliers", inventoryService.getAllSuppliers());
        model.addAttribute("search", search);
        model.addAttribute("item", new InventoryItem());
        model.addAttribute("supplier", new Supplier());
        return "inventory";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        return inventoryService.getAllItems().stream().filter(item -> item.getId().equals(id)).findFirst()
                .map(item -> {
                    model.addAttribute("item", item);
                    model.addAttribute("items", inventoryService.getAllItems());
                    model.addAttribute("suppliers", inventoryService.getAllSuppliers());
                    model.addAttribute("lowStock", inventoryService.getLowStockItems(5));
                    model.addAttribute("supplier", new Supplier());
                    return "edit-inventory";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "Inventory item not found.");
                    return "redirect:/inventory";
                });
    }

    @PostMapping("/items")
    public String addItem(@Valid @ModelAttribute("item") InventoryItem item,
                          BindingResult br,
                          @RequestParam(name = "supplierId", required = false) Long supplierId,
                          Model model,
                          RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("items", inventoryService.getAllItems());
            model.addAttribute("suppliers", inventoryService.getAllSuppliers());
            model.addAttribute("lowStock", inventoryService.getLowStockItems(5));
            model.addAttribute("supplier", new Supplier());
            return "inventory";
        }
        inventoryService.addItem(item, supplierId);
        ra.addFlashAttribute("successMessage", "Inventory item added");
        return "redirect:/inventory";
    }

    @PutMapping("/items/{id}")
    public String updateItem(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("item") InventoryItem item,
                             BindingResult br,
                             @RequestParam(name = "supplierId", required = false) Long supplierId,
                             Model model,
                             RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("items", inventoryService.getAllItems());
            model.addAttribute("suppliers", inventoryService.getAllSuppliers());
            model.addAttribute("lowStock", inventoryService.getLowStockItems(5));
            model.addAttribute("supplier", new Supplier());
            return "edit-inventory";
        }
        inventoryService.updateItem(id, item, supplierId);
        ra.addFlashAttribute("successMessage", "Inventory item updated");
        return "redirect:/inventory";
    }

    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable("id") Long id, RedirectAttributes ra) {
        inventoryService.deleteItem(id);
        ra.addFlashAttribute("successMessage", "Inventory item deleted");
        return "redirect:/inventory";
    }

    @PostMapping("/suppliers")
    public String addSupplier(@Valid @ModelAttribute("supplier") Supplier supplier, BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("items", inventoryService.getAllItems());
            model.addAttribute("suppliers", inventoryService.getAllSuppliers());
            model.addAttribute("lowStock", inventoryService.getLowStockItems(5));
            model.addAttribute("item", new InventoryItem());
            return "inventory";
        }
        inventoryService.addSupplier(supplier);
        ra.addFlashAttribute("successMessage", "Supplier added");
        return "redirect:/inventory";
    }
}

