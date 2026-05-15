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
    public List<MenuItem> getItemsByCategory(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    @Override
    public MenuItem createMenuItem(MenuItem item) {
        return itemRepository.save(item);
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
}

