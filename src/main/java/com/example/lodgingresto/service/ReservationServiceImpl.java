package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Reservation;
import com.example.lodgingresto.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
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
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }
}

