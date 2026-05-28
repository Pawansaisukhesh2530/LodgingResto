package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.ApiResponse;
import com.example.lodgingresto.model.Guest;
import com.example.lodgingresto.service.GuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class ApiGuestController {

    private final GuestService guestService;

    public ApiGuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Guest>>> listAll() {
        List<Guest> data = guestService.getAllGuests();
        return ResponseEntity.ok(new ApiResponse<>("success", "Guests fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Guest>> getById(@PathVariable Long id) {
        Guest data = guestService.getGuestById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Guest not found with id " + id));
        return ResponseEntity.ok(new ApiResponse<>("success", "Guest found", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Guest>> create(@RequestBody Guest guest) {
        Guest saved = guestService.createGuest(guest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Guest created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Guest>> update(@PathVariable Long id, @RequestBody Guest guest) {
        Guest updated = guestService.updateGuest(id, guest);
        return ResponseEntity.ok(new ApiResponse<>("success", "Guest updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Guest deleted"));
    }
}
