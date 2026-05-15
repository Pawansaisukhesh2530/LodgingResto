package com.example.lodgingresto.service;

import com.example.lodgingresto.model.MenuCategory;
import com.example.lodgingresto.model.MenuItem;
import com.example.lodgingresto.model.RestaurantOrder;

import java.util.List;

public interface RestaurantService {
    List<MenuCategory> getAllCategories();
    MenuCategory createCategory(MenuCategory category);
    List<MenuItem> getItemsByCategory(Long categoryId);
    MenuItem createMenuItem(MenuItem item);
    RestaurantOrder createOrder(RestaurantOrder order);
    List<RestaurantOrder> getAllOrders();
}

