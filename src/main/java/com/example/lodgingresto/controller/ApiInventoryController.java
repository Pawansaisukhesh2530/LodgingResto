package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.ApiResponse;
import com.example.lodgingresto.model.InventoryItem;
import com.example.lodgingresto.model.Supplier;
import com.example.lodgingresto.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class ApiInventoryController {

    private final InventoryService inventoryService;

    public ApiInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryItem>>> listAll() {
        List<InventoryItem> data = inventoryService.getAllItems();
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory items fetched", data));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryItem>>> getLowStock(
            @RequestParam(value = "threshold", defaultValue = "10") int threshold) {
        List<InventoryItem> data = inventoryService.getLowStockItems(threshold);
        return ResponseEntity.ok(new ApiResponse<>("success", "Low stock items fetched", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryItem>> create(@RequestBody InventoryItem item) {
        InventoryItem saved = inventoryService.addItem(item);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Inventory item created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryItem>> update(@PathVariable Long id, @RequestBody InventoryItem item) {
        InventoryItem updated = inventoryService.updateItem(id, item);
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory item updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Inventory item deleted"));
    }

    @GetMapping("/suppliers")
    public ResponseEntity<ApiResponse<List<Supplier>>> listSuppliers() {
        List<Supplier> data = inventoryService.getAllSuppliers();
        return ResponseEntity.ok(new ApiResponse<>("success", "Suppliers fetched", data));
    }
}
