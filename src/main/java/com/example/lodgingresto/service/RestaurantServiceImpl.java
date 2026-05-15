package com.example.lodgingresto.service;

import com.example.lodgingresto.model.MenuCategory;
import com.example.lodgingresto.model.MenuItem;
import com.example.lodgingresto.model.OrderItem;
import com.example.lodgingresto.model.RestaurantOrder;
import com.example.lodgingresto.repository.MenuCategoryRepository;
import com.example.lodgingresto.repository.MenuItemRepository;
import com.example.lodgingresto.repository.RestaurantOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository itemRepository;
    private final RestaurantOrderRepository orderRepository;

    public RestaurantServiceImpl(MenuCategoryRepository categoryRepository, MenuItemRepository itemRepository, RestaurantOrderRepository orderRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public MenuCategory createCategory(MenuCategory category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> getItemsByCategory(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    @Override
    public MenuItem createMenuItem(MenuItem item) {
        return itemRepository.save(item);
    }

    @Override
    public MenuItem createMenuItem(MenuItem item, Long categoryId) {
        item.setCategory(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found")));
        return createMenuItem(item);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem item) {
        MenuItem existing = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
        existing.setName(item.getName());
        existing.setDescription(item.getDescription());
        existing.setPrice(item.getPrice());
        existing.setCategory(item.getCategory());
        return itemRepository.save(existing);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem item, Long categoryId) {
        item.setCategory(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found")));
        return updateMenuItem(id, item);
    }

    @Override
    public void deleteMenuItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> getAllMenuItems() {
        return itemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> searchMenuItems(String search, Long categoryId) {
        String normalized = search == null ? "" : search.trim().toLowerCase();
        return itemRepository.findAll().stream()
                .filter(item -> categoryId == null || (item.getCategory() != null && categoryId.equals(item.getCategory().getId())))
                .filter(item -> normalized.isEmpty() || contains(item.getName(), normalized)
                        || contains(item.getDescription(), normalized)
                        || contains(item.getCategory() != null ? item.getCategory().getName() : null, normalized))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuItem> getMenuItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public RestaurantOrder createOrder(RestaurantOrder order) {
        // Calculate totals
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        // set bi-directional links
        for (OrderItem it : order.getItems()) {
            it.setOrder(order);
        }
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }
}

