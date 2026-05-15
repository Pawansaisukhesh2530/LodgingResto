package com.example.lodgingresto.service;

import com.example.lodgingresto.model.Guest;

import java.util.List;
import java.util.Optional;

public interface GuestService {
    Guest createGuest(Guest guest);
    Guest updateGuest(Long id, Guest guest);
    void deleteGuest(Long id);
    List<Guest> getAllGuests();
    List<Guest> searchGuests(String search);
    Optional<Guest> getGuestById(Long id);
}

