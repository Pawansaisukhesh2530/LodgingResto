package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Guest;
import com.example.lodgingresto.model.Reservation;
import com.example.lodgingresto.model.Room;
import com.example.lodgingresto.model.RoomStatus;
import com.example.lodgingresto.repository.GuestRepository;
import com.example.lodgingresto.repository.ReservationRepository;
import com.example.lodgingresto.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository, GuestRepository guestRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation createReservation(Reservation reservation, Long guestId, Long roomId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found"));
        reservation.setGuest(guest);
        reservation.setRoom(room);
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);
        return createReservation(reservation);
    }

    @Override
    public Reservation updateReservation(Long id, Reservation reservation) {
        Reservation existing = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        existing.setGuest(reservation.getGuest());
        existing.setRoom(reservation.getRoom());
        existing.setCheckInDate(reservation.getCheckInDate());
        existing.setCheckOutDate(reservation.getCheckOutDate());
        existing.setTotalGuests(reservation.getTotalGuests());
        existing.setPaymentStatus(reservation.getPaymentStatus());
        return reservationRepository.save(existing);
    }

    @Override
    public Reservation updateReservation(Long id, Reservation reservation, Long guestId, Long roomId) {
        Reservation existing = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (existing.getRoom() != null && !existing.getRoom().getId().equals(roomId)) {
            existing.getRoom().setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(existing.getRoom());
        }
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);

        existing.setGuest(guest);
        existing.setRoom(room);
        existing.setCheckInDate(reservation.getCheckInDate());
        existing.setCheckOutDate(reservation.getCheckOutDate());
        existing.setTotalGuests(reservation.getTotalGuests());
        existing.setPaymentStatus(reservation.getPaymentStatus());
        return reservationRepository.save(existing);
    }

    @Override
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null && reservation.getRoom() != null) {
            reservation.getRoom().setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(reservation.getRoom());
        }
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> searchReservations(String search) {
        String normalized = search == null ? "" : search.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return getAllReservations();
        }
        return reservationRepository.findAll().stream()
                .filter(reservation -> contains(reservation.getGuest() != null ? reservation.getGuest().getFullName() : null, normalized)
                        || contains(reservation.getRoom() != null ? reservation.getRoom().getRoomNumber() : null, normalized)
                        || contains(reservation.getPaymentStatus(), normalized)
                        || String.valueOf(reservation.getTotalGuests()).contains(normalized)
                        || String.valueOf(reservation.getCheckInDate()).contains(normalized)
                        || String.valueOf(reservation.getCheckOutDate()).contains(normalized))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }
}

