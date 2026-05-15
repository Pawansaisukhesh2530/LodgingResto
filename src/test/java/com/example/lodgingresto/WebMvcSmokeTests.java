package com.example.lodgingresto;

import com.example.lodgingresto.controller.AuthController;
import com.example.lodgingresto.controller.DashboardController;
import com.example.lodgingresto.model.Guest;
import com.example.lodgingresto.model.Room;
import com.example.lodgingresto.model.RoomStatus;
import com.example.lodgingresto.model.RoomType;
import com.example.lodgingresto.service.BillingService;
import com.example.lodgingresto.service.EmployeeService;
import com.example.lodgingresto.service.GuestService;
import com.example.lodgingresto.service.InventoryService;
import com.example.lodgingresto.service.ReservationService;
import com.example.lodgingresto.service.RestaurantService;
import com.example.lodgingresto.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebMvcSmokeTests {

    @Test
    void loginControllerReturnsLoginView() {
        assertEquals("login", new AuthController().login());
    }

    @Test
    void dashboardControllerPopulatesDashboardView() {
        DashboardController controller = new DashboardController(
                new RoomServiceStub(),
                new GuestServiceStub(),
                new ReservationServiceStub(),
                new EmployeeServiceStub(),
                new InventoryServiceStub(),
                new RestaurantServiceStub(),
                new BillingServiceStub());

        Model model = new ExtendedModelMap();
        assertEquals("dashboard", controller.dashboard(model));
        assertEquals(10L, model.getAttribute("totalRooms"));
        assertEquals(4L, model.getAttribute("availableRooms"));
    }

    private static class RoomServiceStub implements RoomService {
        @Override public List<Room> getAllRooms(String search, RoomType roomType, RoomStatus status) { return Collections.emptyList(); }
        @Override public List<Room> getAllRooms() { return Collections.emptyList(); }
        @Override public Optional<Room> getRoomById(Long id) { return Optional.empty(); }
        @Override public Room createRoom(Room room) { return room; }
        @Override public Room updateRoom(Long id, Room room) { return room; }
        @Override public void deleteRoom(Long id) { }
        @Override public long countAllRooms() { return 10L; }
        @Override public long countAvailableRooms() { return 4L; }
        @Override public long countOccupiedRooms() { return 3L; }
        @Override public long countReservedRooms() { return 2L; }
        @Override public long countMaintenanceRooms() { return 1L; }
        @Override public BigDecimal calculateRevenue() { return new BigDecimal("1250.00"); }
    }

    private static class GuestServiceStub implements GuestService {
        @Override public Guest createGuest(Guest guest) { return guest; }
        @Override public Guest updateGuest(Long id, Guest guest) { return guest; }
        @Override public void deleteGuest(Long id) { }
        @Override public List<Guest> getAllGuests() { return Collections.emptyList(); }
        @Override public List<Guest> searchGuests(String search) { return Collections.emptyList(); }
        @Override public Optional<Guest> getGuestById(Long id) { return Optional.empty(); }
    }

    private static class ReservationServiceStub implements ReservationService {
        @Override public com.example.lodgingresto.model.Reservation createReservation(com.example.lodgingresto.model.Reservation reservation) { return reservation; }
        @Override public com.example.lodgingresto.model.Reservation updateReservation(Long id, com.example.lodgingresto.model.Reservation reservation) { return reservation; }
        @Override public void deleteReservation(Long id) { }
        @Override public List<com.example.lodgingresto.model.Reservation> getAllReservations() { return Collections.emptyList(); }
        @Override public List<com.example.lodgingresto.model.Reservation> searchReservations(String search) { return Collections.emptyList(); }
        @Override public Optional<com.example.lodgingresto.model.Reservation> getReservationById(Long id) { return Optional.empty(); }
        @Override public com.example.lodgingresto.model.Reservation createReservation(com.example.lodgingresto.model.Reservation reservation, Long guestId, Long roomId) { return reservation; }
        @Override public com.example.lodgingresto.model.Reservation updateReservation(Long id, com.example.lodgingresto.model.Reservation reservation, Long guestId, Long roomId) { return reservation; }
    }

    private static class EmployeeServiceStub implements EmployeeService {
        @Override public com.example.lodgingresto.model.Employee createEmployee(com.example.lodgingresto.model.Employee e) { return e; }
        @Override public com.example.lodgingresto.model.Employee updateEmployee(Long id, com.example.lodgingresto.model.Employee e) { return e; }
        @Override public void deleteEmployee(Long id) { }
        @Override public List<com.example.lodgingresto.model.Employee> getAllEmployees() { return Collections.emptyList(); }
        @Override public List<com.example.lodgingresto.model.Employee> searchEmployees(String search) { return Collections.emptyList(); }
        @Override public Optional<com.example.lodgingresto.model.Employee> getEmployeeById(Long id) { return Optional.empty(); }
    }

    private static class InventoryServiceStub implements InventoryService {
        @Override public com.example.lodgingresto.model.InventoryItem addItem(com.example.lodgingresto.model.InventoryItem item) { return item; }
        @Override public com.example.lodgingresto.model.InventoryItem updateItem(Long id, com.example.lodgingresto.model.InventoryItem item) { return item; }
        @Override public com.example.lodgingresto.model.InventoryItem addItem(com.example.lodgingresto.model.InventoryItem item, Long supplierId) { return item; }
        @Override public com.example.lodgingresto.model.InventoryItem updateItem(Long id, com.example.lodgingresto.model.InventoryItem item, Long supplierId) { return item; }
        @Override public void deleteItem(Long id) { }
        @Override public List<com.example.lodgingresto.model.InventoryItem> getAllItems() { return Collections.emptyList(); }
        @Override public List<com.example.lodgingresto.model.InventoryItem> searchItems(String search) { return Collections.emptyList(); }
        @Override public List<com.example.lodgingresto.model.InventoryItem> getLowStockItems(int threshold) { return Collections.emptyList(); }
        @Override public com.example.lodgingresto.model.Supplier addSupplier(com.example.lodgingresto.model.Supplier s) { return s; }
        @Override public List<com.example.lodgingresto.model.Supplier> getAllSuppliers() { return Collections.emptyList(); }
    }

    private static class RestaurantServiceStub implements RestaurantService {
        @Override public List<com.example.lodgingresto.model.MenuCategory> getAllCategories() { return Collections.emptyList(); }
        @Override public com.example.lodgingresto.model.MenuCategory createCategory(com.example.lodgingresto.model.MenuCategory category) { return category; }
        @Override public Optional<com.example.lodgingresto.model.MenuCategory> getCategoryById(Long id) { return Optional.empty(); }
        @Override public List<com.example.lodgingresto.model.MenuItem> getItemsByCategory(Long categoryId) { return Collections.emptyList(); }
        @Override public com.example.lodgingresto.model.MenuItem createMenuItem(com.example.lodgingresto.model.MenuItem item) { return item; }
        @Override public com.example.lodgingresto.model.MenuItem createMenuItem(com.example.lodgingresto.model.MenuItem item, Long categoryId) { return item; }
        @Override public com.example.lodgingresto.model.MenuItem updateMenuItem(Long id, com.example.lodgingresto.model.MenuItem item) { return item; }
        @Override public com.example.lodgingresto.model.MenuItem updateMenuItem(Long id, com.example.lodgingresto.model.MenuItem item, Long categoryId) { return item; }
        @Override public void deleteMenuItem(Long id) { }
        @Override public List<com.example.lodgingresto.model.MenuItem> getAllMenuItems() { return Collections.emptyList(); }
        @Override public List<com.example.lodgingresto.model.MenuItem> searchMenuItems(String search, Long categoryId) { return Collections.emptyList(); }
        @Override public Optional<com.example.lodgingresto.model.MenuItem> getMenuItemById(Long id) { return Optional.empty(); }
        @Override public com.example.lodgingresto.model.RestaurantOrder createOrder(com.example.lodgingresto.model.RestaurantOrder order) { return order; }
        @Override public List<com.example.lodgingresto.model.RestaurantOrder> getAllOrders() { return Collections.emptyList(); }
    }

    private static class BillingServiceStub implements BillingService {
        @Override public List<com.example.lodgingresto.model.Invoice> getAllInvoices() { return Collections.emptyList(); }
        @Override public Optional<com.example.lodgingresto.model.Invoice> getInvoiceById(Long id) { return Optional.empty(); }
        @Override public com.example.lodgingresto.model.Invoice createInvoice(com.example.lodgingresto.model.Invoice invoice, Long reservationId, Long orderId) { return invoice; }
        @Override public BigDecimal getRevenue() { return new BigDecimal("1250.00"); }
        @Override public BigDecimal getTaxAmount() { return new BigDecimal("225.00"); }
    }
}
