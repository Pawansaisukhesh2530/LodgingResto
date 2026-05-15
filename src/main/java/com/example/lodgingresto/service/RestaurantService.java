package com.example.lodgingresto.service;

import com.example.lodgingresto.model.MenuCategory;
import com.example.lodgingresto.model.MenuItem;
import com.example.lodgingresto.model.RestaurantOrder;

import java.util.List;

public interface RestaurantService {
    List<MenuCategory> getAllCategories();
    MenuCategory createCategory(MenuCategory category);
    java.util.Optional<MenuCategory> getCategoryById(Long id);
    List<MenuItem> getItemsByCategory(Long categoryId);
    MenuItem createMenuItem(MenuItem item);
    MenuItem createMenuItem(MenuItem item, Long categoryId);
    MenuItem updateMenuItem(Long id, MenuItem item);
    MenuItem updateMenuItem(Long id, MenuItem item, Long categoryId);
    void deleteMenuItem(Long id);
    List<MenuItem> getAllMenuItems();
    List<MenuItem> searchMenuItems(String search, Long categoryId);
    java.util.Optional<MenuItem> getMenuItemById(Long id);
    RestaurantOrder createOrder(RestaurantOrder order);
    List<RestaurantOrder> getAllOrders();
}

