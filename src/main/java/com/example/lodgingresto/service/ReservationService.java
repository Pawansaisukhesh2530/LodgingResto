package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation createReservation(Reservation reservation);
    Reservation updateReservation(Long id, Reservation reservation);
    void deleteReservation(Long id);
    List<Reservation> getAllReservations();
    List<Reservation> searchReservations(String search);
    Optional<Reservation> getReservationById(Long id);

    Reservation createReservation(Reservation reservation, Long guestId, Long roomId);
    Reservation updateReservation(Long id, Reservation reservation, Long guestId, Long roomId);
}

