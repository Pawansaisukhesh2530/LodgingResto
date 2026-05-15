package com.example.lodgingresto.service;

import com.example.lodgingresto.model.InventoryItem;
import com.example.lodgingresto.model.Supplier;
import com.example.lodgingresto.repository.InventoryItemRepository;
import com.example.lodgingresto.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository itemRepository;
    private final SupplierRepository supplierRepository;

    public InventoryServiceImpl(InventoryItemRepository itemRepository, SupplierRepository supplierRepository) {
        this.itemRepository = itemRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public InventoryItem addItem(InventoryItem item) { return itemRepository.save(item); }

    @Override
    public InventoryItem addItem(InventoryItem item, Long supplierId) {
        if (supplierId != null) {
            item.setSupplier(supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found")));
        }
        return addItem(item);
    }

    @Override
    public InventoryItem updateItem(Long id, InventoryItem item) {
        InventoryItem existing = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        existing.setName(item.getName());
        existing.setDescription(item.getDescription());
        existing.setQuantity(item.getQuantity());
        existing.setSupplier(item.getSupplier());
        return itemRepository.save(existing);
    }

    @Override
    public InventoryItem updateItem(Long id, InventoryItem item, Long supplierId) {
        item.setSupplier(supplierId == null ? null : supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found")));
        return updateItem(id, item);
    }

    @Override
    public void deleteItem(Long id) { itemRepository.deleteById(id); }

    @Override
    public List<InventoryItem> getAllItems() { return itemRepository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItem> searchItems(String search) {
        String normalized = search == null ? "" : search.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return getAllItems();
        }
        return itemRepository.findAll().stream()
                .filter(item -> contains(item.getName(), normalized)
                        || contains(item.getDescription(), normalized)
                        || (item.getSupplier() != null && contains(item.getSupplier().getName(), normalized))
                        || String.valueOf(item.getQuantity()).contains(normalized))
                .toList();
    }

    @Override
    public List<InventoryItem> getLowStockItems(int threshold) { return itemRepository.findByQuantityLessThan(threshold); }

    @Override
    public Supplier addSupplier(Supplier s) { return supplierRepository.save(s); }

    @Override
    public List<Supplier> getAllSuppliers() { return supplierRepository.findAll(); }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }
}

