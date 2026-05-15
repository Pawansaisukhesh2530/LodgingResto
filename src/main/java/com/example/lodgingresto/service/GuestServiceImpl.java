package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Guest;
import com.example.lodgingresto.repository.GuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public Guest createGuest(Guest guest) {
        return guestRepository.save(guest);
    }

    @Override
    public Guest updateGuest(Long id, Guest guest) {
        Guest existing = guestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        existing.setFullName(guest.getFullName());
        existing.setPhoneNumber(guest.getPhoneNumber());
        existing.setEmail(guest.getEmail());
        existing.setAddress(guest.getAddress());
        existing.setIdProofNumber(guest.getIdProofNumber());
        return guestRepository.save(existing);
    }

    @Override
    public void deleteGuest(Long id) {
        guestRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Guest> getGuestById(Long id) {
        return guestRepository.findById(id);
    }
}

