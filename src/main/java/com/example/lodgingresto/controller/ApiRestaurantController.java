package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.ApiResponse;
import com.example.lodgingresto.model.MenuCategory;
import com.example.lodgingresto.model.MenuItem;
import com.example.lodgingresto.model.RestaurantOrder;
import com.example.lodgingresto.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
public class ApiRestaurantController {

    private final RestaurantService restaurantService;

    public ApiRestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/menu")
    public ResponseEntity<ApiResponse<List<MenuCategory>>> getMenu() {
        List<MenuCategory> data = restaurantService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>("success", "Menu fetched", data));
    }

    @GetMapping("/menu/items")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenuItems() {
        List<MenuItem> data = restaurantService.getAllMenuItems();
        return ResponseEntity.ok(new ApiResponse<>("success", "Menu items fetched", data));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<RestaurantOrder>>> listOrders() {
        List<RestaurantOrder> data = restaurantService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse<>("success", "Orders fetched", data));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<RestaurantOrder>> createOrder(@RequestBody RestaurantOrder order) {
        RestaurantOrder saved = restaurantService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Order created", saved));
    }
}
