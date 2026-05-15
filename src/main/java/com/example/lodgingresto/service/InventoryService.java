package com.example.lodgingresto.service;

import com.example.lodgingresto.model.InventoryItem;
import com.example.lodgingresto.model.Supplier;

import java.util.List;

public interface InventoryService {
    InventoryItem addItem(InventoryItem item);
    InventoryItem updateItem(Long id, InventoryItem item);
    void deleteItem(Long id);
    List<InventoryItem> getAllItems();
    List<InventoryItem> getLowStockItems(int threshold);
    Supplier addSupplier(Supplier s);
    List<Supplier> getAllSuppliers();
}

