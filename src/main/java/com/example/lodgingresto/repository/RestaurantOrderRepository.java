package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
}

