package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}

