package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.ApiResponse;
import com.example.lodgingresto.model.Room;
import com.example.lodgingresto.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class ApiRoomController {

    private final RoomService roomService;

    public ApiRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Room>>> listAll() {
        List<Room> data = roomService.getAllRooms();
        return ResponseEntity.ok(new ApiResponse<>("success", "Rooms fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Room>> getById(@PathVariable Long id) {
        Room data = roomService.getRoomById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Room not found with id " + id));
        return ResponseEntity.ok(new ApiResponse<>("success", "Room found", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Room>> create(@RequestBody Room room) {
        Room saved = roomService.createRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("success", "Room created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Room>> update(@PathVariable Long id, @RequestBody Room room) {
        Room updated = roomService.updateRoom(id, room);
        return ResponseEntity.ok(new ApiResponse<>("success", "Room updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Room deleted"));
    }
}
