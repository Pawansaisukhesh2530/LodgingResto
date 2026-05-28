package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.ApiResponse;
import com.example.lodgingresto.model.Reservation;
import com.example.lodgingresto.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ApiReservationController {

    private final ReservationService reservationService;

    public ApiReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Reservation>>> listAll() {
        List<Reservation> data = reservationService.getAllReservations();
        return ResponseEntity.ok(new ApiResponse<>("success", "Reservations fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Reservation>> getById(@PathVariable Long id) {
        Reservation data = reservationService.getReservationById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Reservation not found with id " + id));
        return ResponseEntity.ok(new ApiResponse<>("success", "Reservation found", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Reservation>> create(@RequestBody Reservation reservation) {
        Reservation saved = reservationService.createReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Reservation created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Reservation>> update(@PathVariable Long id, @RequestBody Reservation reservation) {
        Reservation updated = reservationService.updateReservation(id, reservation);
        return ResponseEntity.ok(new ApiResponse<>("success", "Reservation updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Reservation deleted"));
    }
}
