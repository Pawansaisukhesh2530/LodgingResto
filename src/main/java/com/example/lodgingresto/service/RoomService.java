package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Room;
import com.example.lodgingresto.model.RoomStatus;
import com.example.lodgingresto.model.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomService {

    List<Room> getAllRooms(String search, RoomType roomType, RoomStatus status);

    List<Room> getAllRooms();

    Optional<Room> getRoomById(Long id);

    Room createRoom(Room room);

    Room updateRoom(Long id, Room room);

    void deleteRoom(Long id);

    long countAllRooms();

    long countAvailableRooms();

    long countOccupiedRooms();

    java.math.BigDecimal calculateRevenue();
}

