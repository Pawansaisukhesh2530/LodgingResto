package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Room;
import com.example.lodgingresto.model.RoomStatus;
import com.example.lodgingresto.model.RoomType;
import com.example.lodgingresto.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms(String search, RoomType roomType, RoomStatus status) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase();

        return roomRepository.findAll().stream()
                .filter(room -> normalizedSearch.isEmpty() || matchesSearch(room, normalizedSearch))
                .filter(room -> roomType == null || room.getRoomType() == roomType)
                .filter(room -> status == null || room.getStatus() == status)
                .sorted(Comparator.comparing(Room::getRoomNumber, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll().stream()
                .sorted(Comparator.comparing(Room::getRoomNumber, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room createRoom(Room room) {
        if (roomRepository.existsByRoomNumberIgnoreCase(room.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists");
        }
        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Long id, Room room) {
        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (roomRepository.existsByRoomNumberIgnoreCaseAndIdNot(room.getRoomNumber(), id)) {
            throw new IllegalArgumentException("Room number already exists");
        }

        existing.setRoomNumber(room.getRoomNumber());
        existing.setRoomType(room.getRoomType());
        existing.setPrice(room.getPrice());
        existing.setStatus(room.getStatus());
        existing.setDescription(room.getDescription());
        return roomRepository.save(existing);
    }

    @Override
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new IllegalArgumentException("Room not found");
        }

        try {
            entityManager.createNativeQuery("delete from room_images where room_id = :roomId")
                    .setParameter("roomId", id)
                    .executeUpdate();
        } catch (Exception ex) {
            // Ignore cleanup failures for environments where the legacy room_images table is absent.
        }

        roomRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllRooms() {
        return roomRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAvailableRooms() {
        return roomRepository.countByStatus(RoomStatus.AVAILABLE);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOccupiedRooms() {
        return roomRepository.countByStatus(RoomStatus.OCCUPIED);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateRevenue() {
        BigDecimal occupiedRevenue = roomRepository.sumPriceByStatus(RoomStatus.OCCUPIED);
        return occupiedRevenue == null ? BigDecimal.ZERO : occupiedRevenue;
    }

    private boolean matchesSearch(Room room, String search) {
        return containsIgnoreCase(room.getRoomNumber(), search)
                || containsIgnoreCase(room.getDescription(), search)
                || containsIgnoreCase(room.getRoomType() != null ? room.getRoomType().getLabel() : null, search)
                || containsIgnoreCase(room.getStatus() != null ? room.getStatus().getLabel() : null, search);
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }
}

