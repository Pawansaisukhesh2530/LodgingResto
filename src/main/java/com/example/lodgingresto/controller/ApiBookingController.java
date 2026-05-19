package com.example.lodgingresto.controller;

import com.example.lodgingresto.dto.ApiResponse;
import com.example.lodgingresto.dto.BookingRequest;
import com.example.lodgingresto.dto.BookingResponse;
import com.example.lodgingresto.model.Guest;
import com.example.lodgingresto.model.Reservation;
import com.example.lodgingresto.model.Room;
import com.example.lodgingresto.model.RoomStatus;
import com.example.lodgingresto.model.RoomType;
import com.example.lodgingresto.model.ApiLog;
import com.example.lodgingresto.repository.GuestRepository;
import com.example.lodgingresto.repository.RoomRepository;
import com.example.lodgingresto.service.ApiUsageService;
import com.example.lodgingresto.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class ApiBookingController {

    private final ReservationService reservationService;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final ApiUsageService apiUsageService;

    public ApiBookingController(ReservationService reservationService, GuestRepository guestRepository, RoomRepository roomRepository, ApiUsageService apiUsageService) {
        this.reservationService = reservationService;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.apiUsageService = apiUsageService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> listBookings() {
        Instant start = Instant.now();
        List<BookingResponse> data = reservationService.getAllReservations().stream().map(this::toDto).collect(Collectors.toList());
        ApiResponse<List<BookingResponse>> resp = new ApiResponse<>("success", "Bookings fetched", data);
        recordLog("/api/bookings", "GET", start, HttpStatus.OK.value(), null);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createBooking(@Valid @RequestBody BookingRequest req) {
        Instant start = Instant.now();
        // find or create guest
        Guest guest = guestRepository.findAll().stream()
                .filter(g -> g.getFullName() != null && g.getFullName().equalsIgnoreCase(req.getGuestName()))
                .findFirst().orElseGet(() -> {
                    Guest g = new Guest();
                    g.setFullName(req.getGuestName());
                    g.setPhoneNumber("unknown");
                    return guestRepository.save(g);
                });

        // resolve room type
        RoomType rt = parseRoomType(req.getRoomType());
        List<Room> candidates = roomRepository.findByRoomTypeAndStatus(rt, RoomStatus.AVAILABLE);
        if (candidates.isEmpty()) {
            recordLog("/api/bookings", "POST", start, HttpStatus.BAD_REQUEST.value(), req.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("error", "No available rooms for requested type"));
        }
        Room chosen = candidates.get(0);

        Reservation res = new Reservation();
        res.setGuest(guest);
        res.setRoom(chosen);
        res.setCheckInDate(req.getCheckInDate());
        res.setCheckOutDate(req.getCheckOutDate());
        res.setPaymentStatus(req.getPaymentStatus());
        Reservation saved = reservationService.createReservation(res);

        // mark room reserved
        chosen.setStatus(RoomStatus.RESERVED);
        roomRepository.save(chosen);

        recordLog("/api/bookings", "POST", start, HttpStatus.CREATED.value(), req.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("success", "Booking created successfully", saved.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateBooking(@PathVariable Long id, @Valid @RequestBody BookingRequest req) {
        Instant start = Instant.now();
        Reservation existing = reservationService.getReservationById(id).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // update guest (try find by name or create)
        Guest guest = guestRepository.findAll().stream()
                .filter(g -> g.getFullName() != null && g.getFullName().equalsIgnoreCase(req.getGuestName()))
                .findFirst().orElseGet(() -> {
                    Guest g = new Guest();
                    g.setFullName(req.getGuestName());
                    g.setPhoneNumber("unknown");
                    return guestRepository.save(g);
                });

        RoomType rt = parseRoomType(req.getRoomType());
        // try keep same room if it matches type otherwise find new available
        Room room = existing.getRoom();
        if (room == null || room.getRoomType() != rt) {
            List<Room> candidates = roomRepository.findByRoomTypeAndStatus(rt, RoomStatus.AVAILABLE);
            if (candidates.isEmpty()) {
                recordLog("/api/bookings", "PUT", start, HttpStatus.BAD_REQUEST.value(), req.toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("error", "No available rooms for requested type"));
            }
            room = candidates.get(0);
        }

        existing.setGuest(guest);
        existing.setRoom(room);
        existing.setCheckInDate(req.getCheckInDate());
        existing.setCheckOutDate(req.getCheckOutDate());
        existing.setPaymentStatus(req.getPaymentStatus());
        reservationService.updateReservation(id, existing);

        recordLog("/api/bookings/"+id, "PUT", start, HttpStatus.OK.value(), req.toString());
        return ResponseEntity.ok(new ApiResponse<>("success", "Booking updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> cancelBooking(@PathVariable Long id) {
        Instant start = Instant.now();
        reservationService.deleteReservation(id);
        recordLog("/api/bookings/"+id, "DELETE", start, HttpStatus.NO_CONTENT.value(), null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>("success", "Booking cancelled"));
    }

    private BookingResponse toDto(Reservation r) {
        BookingResponse dto = new BookingResponse();
        dto.setId(r.getId());
        dto.setGuestName(r.getGuest() == null ? null : r.getGuest().getFullName());
        dto.setRoomNumber(r.getRoom() == null ? null : r.getRoom().getRoomNumber());
        dto.setRoomType(r.getRoom() == null ? null : r.getRoom().getRoomType() == null ? null : r.getRoom().getRoomType().name());
        dto.setCheckInDate(r.getCheckInDate());
        dto.setCheckOutDate(r.getCheckOutDate());
        dto.setTotalGuests(r.getTotalGuests());
        dto.setPaymentStatus(r.getPaymentStatus());
        return dto;
    }

    private RoomType parseRoomType(String raw) {
        if (raw == null) return RoomType.SINGLE;
        try {
            return RoomType.valueOf(raw.trim().toUpperCase());
        } catch (Exception ex) {
            // try match by label
            for (RoomType rt : RoomType.values()) {
                if (rt.getLabel().equalsIgnoreCase(raw.trim())) return rt;
            }
            return RoomType.SINGLE;
        }
    }

    private void recordLog(String endpoint, String method, Instant start, int status, String requestBody) {
        ApiLog log = new ApiLog();
        log.setEndpoint(endpoint);
        log.setMethod(method);
        log.setRequestTime(start);
        log.setResponseStatus(status);
        log.setDurationMs(ChronoUnit.MILLIS.between(start, Instant.now()));
        log.setRequestBody(requestBody);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) log.setUsername(auth.getName());
        apiUsageService.record(log);
    }
}

