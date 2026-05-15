package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}

