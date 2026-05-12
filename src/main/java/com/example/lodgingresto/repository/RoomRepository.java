package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.Room;
import com.example.lodgingresto.model.RoomStatus;
import com.example.lodgingresto.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumberIgnoreCase(String roomNumber);

    boolean existsByRoomNumberIgnoreCaseAndIdNot(String roomNumber, Long id);

    List<Room> findByRoomNumberContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String roomNumber, String description);

    List<Room> findByRoomType(RoomType roomType);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByRoomTypeAndStatus(RoomType roomType, RoomStatus status);

    long countByStatus(RoomStatus status);

    @Query("select coalesce(sum(r.price), 0) from Room r where r.status = :status")
    java.math.BigDecimal sumPriceByStatus(@Param("status") RoomStatus status);
}

