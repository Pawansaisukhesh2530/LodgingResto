package com.example.lodgingresto.config;

import com.example.lodgingresto.model.*;
import com.example.lodgingresto.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner init(RoleRepository roleRepository,
                           UserRepository userRepository,
                           RoomRepository roomRepository,
                           GuestRepository guestRepository,
                           ReservationRepository reservationRepository,
                           MenuCategoryRepository menuCategoryRepository,
                           MenuItemRepository menuItemRepository,
                           RestaurantOrderRepository restaurantOrderRepository,
                           InvoiceRepository invoiceRepository,
                           ApiLogRepository apiLogRepository,
                           EmployeeRepository employeeRepository,
                           SupplierRepository supplierRepository,
                            InventoryItemRepository inventoryItemRepository,
                            NotificationRepository notificationRepository,
                            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Create security roles
            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role("ADMIN")));
            Role receptionistRole = roleRepository.findByName("RECEPTIONIST").orElseGet(() -> roleRepository.save(new Role("RECEPTIONIST")));
            Role restaurantRole = roleRepository.findByName("RESTAURANT_STAFF").orElseGet(() -> roleRepository.save(new Role("RESTAURANT_STAFF")));
            Role managerRole = roleRepository.findByName("MANAGER").orElseGet(() -> roleRepository.save(new Role("MANAGER")));

            // 2. Create admin user if not present
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setEnabled(true);
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                roles.add(managerRole);
                admin.setRoles(roles);
                userRepository.save(admin);
                logger.info("Seeded default admin user (username=admin, password=123456)");
            }

            // 3. Create demo rooms
            if (roomRepository.count() == 0) {
                createRoom(roomRepository, "101", RoomType.SINGLE, 1500.00, RoomStatus.AVAILABLE, "Charming cozy single room overlooking the quiet rear garden courtyard.");
                createRoom(roomRepository, "102", RoomType.SINGLE, 1600.00, RoomStatus.OCCUPIED, "Charming single setup with high speed workspace, perfect for corporate executives.");
                createRoom(roomRepository, "201", RoomType.DOUBLE, 2500.00, RoomStatus.AVAILABLE, "Spacious double bed layout featuring classic dark wood vanity and walk-in shower.");
                createRoom(roomRepository, "202", RoomType.DOUBLE, 2700.00, RoomStatus.OCCUPIED, "Premium double suite with sweeping balcony access and automated ambient lighting.");
                createRoom(roomRepository, "301", RoomType.DELUXE, 4500.00, RoomStatus.AVAILABLE, "High-floor deluxe experience offering beautiful city skyline panoramas.");
                createRoom(roomRepository, "302", RoomType.DELUXE, 4800.00, RoomStatus.RESERVED, "Elegant deluxe sanctuary with marble bathroom fixtures and walk-in closet.");
                createRoom(roomRepository, "401", RoomType.SUITE, 7500.00, RoomStatus.AVAILABLE, "Grand operations suite featuring private drawing room, reading desk and wet bar.");
                createRoom(roomRepository, "402", RoomType.SUITE, 8000.00, RoomStatus.MAINTENANCE, "Luxurious master suite receiving periodic inspections and hardware updates.");
                createRoom(roomRepository, "501", RoomType.PRESIDENTIAL, 18000.00, RoomStatus.OCCUPIED, "The peak of luxury, panoramic harbor view and private jacuzzi.");
                createRoom(roomRepository, "502", RoomType.PRESIDENTIAL, 19500.00, RoomStatus.AVAILABLE, "Premium penthouse with panoramic lounge deck and personal infinity pool details.");
                createRoom(roomRepository, "103", RoomType.FAMILY, 3500.00, RoomStatus.AVAILABLE, "Three-bed family layout built for absolute group comfort and safety.");
                createRoom(roomRepository, "104", RoomType.FAMILY, 3800.00, RoomStatus.OCCUPIED, "Modern family room complete with kid-friendly board games and dynamic entertainment unit.");
                createRoom(roomRepository, "203", RoomType.DOUBLE, 2400.00, RoomStatus.MAINTENANCE, "Classic double bed setup currently receiving routine carpet deep-cleaning.");
                createRoom(roomRepository, "303", RoomType.DELUXE, 4600.00, RoomStatus.AVAILABLE, "Deluxe sanctuary with ergo-chair workstation and private capsule espresso bar.");
                createRoom(roomRepository, "403", RoomType.SUITE, 7800.00, RoomStatus.RESERVED, "Elite suite configuration designed for long-stay leisure travelers.");
                logger.info("Seeded 15 detailed room configurations.");
            }

            // 4. Create demo guests
            if (guestRepository.count() == 0) {
                createGuest(guestRepository, "Aarav Sharma", "+919876543210", "aarav.sharma@gmail.com", "12 Park Street, New Delhi, India", "PASSPORT-IND9281");
                createGuest(guestRepository, "Aditi Patel", "+918765432109", "aditi.patel@yahoo.com", "88 Marine Drive, Mumbai, India", "DL-MH038102");
                createGuest(guestRepository, "John Smith", "+14155552671", "john.smith@gmail.com", "101 Pine St, San Francisco, USA", "PASS-US982182");
                createGuest(guestRepository, "Priya Nair", "+917654321098", "priya.nair@outlook.com", "45 residency Road, Bangalore, India", "AADHAR-87612891");
                createGuest(guestRepository, "Rohan Das", "+916543210987", "rohan.das@gmail.com", "7B Elgin Road, Kolkata, India", "PAN-CSK918A");
                createGuest(guestRepository, "Sarah Jenkins", "+442079460192", "sarah.j@jenkins.co.uk", "22 Baker St, London, UK", "PASS-GB928122");
                createGuest(guestRepository, "Kabir Mehta", "+919988776655", "kabir.mehta@gmail.com", "14 CG Road, Ahmedabad, India", "DL-GJ019281");
                createGuest(guestRepository, "Elena Petrova", "+79101234567", "elena.p@yandex.ru", "45 Nevsky Prospekt, Moscow, Russia", "PASS-RU810291");
                createGuest(guestRepository, "Vikram Singh", "+919123456789", "vikram.s@singh.in", "102 Amer Road, Jaipur, India", "VOTER-JP09819");
                createGuest(guestRepository, "Meera Iyer", "+918234567890", "meera.iyer@gmail.com", "18 Cathedral Road, Chennai, India", "AADHAR-92810281");
                logger.info("Seeded 10 guest profiles.");
            }

            // 5. Create demo reservations
            if (reservationRepository.count() == 0) {
                List<Guest> guests = guestRepository.findAll();
                List<Room> rooms = roomRepository.findAll();

                if (!guests.isEmpty() && rooms.size() >= 10) {
                    createReservation(reservationRepository, guests.get(0), rooms.stream().filter(r -> r.getRoomNumber().equals("102")).findFirst().get(), LocalDate.now().minusDays(3), LocalDate.now().plusDays(2), 1, "PAID");
                    createReservation(reservationRepository, guests.get(1), rooms.stream().filter(r -> r.getRoomNumber().equals("202")).findFirst().get(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), 2, "PAID");
                    createReservation(reservationRepository, guests.get(2), rooms.stream().filter(r -> r.getRoomNumber().equals("302")).findFirst().get(), LocalDate.now().plusDays(2), LocalDate.now().plusDays(7), 2, "PENDING");
                    createReservation(reservationRepository, guests.get(3), rooms.stream().filter(r -> r.getRoomNumber().equals("501")).findFirst().get(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(4), 2, "PAID");
                    createReservation(reservationRepository, guests.get(4), rooms.stream().filter(r -> r.getRoomNumber().equals("403")).findFirst().get(), LocalDate.now().plusDays(4), LocalDate.now().plusDays(9), 1, "PENDING");
                    createReservation(reservationRepository, guests.get(5), rooms.stream().filter(r -> r.getRoomNumber().equals("104")).findFirst().get(), LocalDate.now().minusDays(2), LocalDate.now().plusDays(3), 3, "PAID");
                    createReservation(reservationRepository, guests.get(6), rooms.stream().filter(r -> r.getRoomNumber().equals("101")).findFirst().get(), LocalDate.now().minusDays(10), LocalDate.now().minusDays(7), 1, "PAID");
                    createReservation(reservationRepository, guests.get(7), rooms.stream().filter(r -> r.getRoomNumber().equals("201")).findFirst().get(), LocalDate.now().minusDays(15), LocalDate.now().minusDays(11), 2, "PAID");
                    logger.info("Seeded 8 reservation bookings.");
                }
            }

            // 6. Create menu categories
            if (menuCategoryRepository.count() == 0) {
                createCategory(menuCategoryRepository, "Starters");
                createCategory(menuCategoryRepository, "Mains");
                createCategory(menuCategoryRepository, "Desserts");
                createCategory(menuCategoryRepository, "Beverages");
                logger.info("Seeded 4 menu categories.");
            }

            // 7. Create menu items
            if (menuItemRepository.count() == 0) {
                MenuCategory starters = menuCategoryRepository.findByName("Starters").orElse(null);
                MenuCategory mains = menuCategoryRepository.findByName("Mains").orElse(null);
                MenuCategory desserts = menuCategoryRepository.findByName("Desserts").orElse(null);
                MenuCategory beverages = menuCategoryRepository.findByName("Beverages").orElse(null);

                createMenuItem(menuItemRepository, "Paneer Tikka", "Marinated cottage cheese cubes chargrilled with bell peppers and spices.", 280.00, starters);
                createMenuItem(menuItemRepository, "Crispy Spring Rolls", "Golden fried pastry wraps filled with fresh seasoned shredded vegetables.", 220.00, starters);
                createMenuItem(menuItemRepository, "Spicy Chicken Wings", "Juicy roasted chicken wings glazed in hot chili pepper barbecue reduction.", 320.00, starters);

                createMenuItem(menuItemRepository, "Butter Chicken", "Succulent tandoori roasted chicken pieces slow-cooked in rich tomato cream gravy.", 420.00, mains);
                createMenuItem(menuItemRepository, "Dal Makhani", "Creamy black lentils simmered overnight with heavy butter and aromatic spices.", 310.00, mains);
                createMenuItem(menuItemRepository, "Margarita Pizza", "Hand-tossed stone-baked flatbread topped with classic pomodoro sauce and fresh basil.", 380.00, mains);
                createMenuItem(menuItemRepository, "Kadai Paneer", "Cottage cheese wok-tossed with fresh green bell peppers and coarse ground spices.", 340.00, mains);

                createMenuItem(menuItemRepository, "Warm Fudge Brownie", "Rich chocolate brownie served hot with warm dark chocolate ganache glaze.", 180.00, desserts);
                createMenuItem(menuItemRepository, "Gulab Jamun Duo", "Spongy caramelized milk dumplings soaked in warm rosewater saffron sugar syrup.", 120.00, desserts);
                createMenuItem(menuItemRepository, "New York Cheesecake", "Silky smooth cream cheese slice baked on a buttery honey graham cracker crust.", 240.00, desserts);

                createMenuItem(menuItemRepository, "Mango Lassi", "Traditional rich churned sweet yogurt blended with fresh Alphonso mango pulp.", 140.00, beverages);
                createMenuItem(menuItemRepository, "Fresh Mint Mojito", "A cooling blend of lime muddled with fresh mint leaves, cane sugar, and club soda.", 160.00, beverages);
                logger.info("Seeded 12 culinary menu items.");
            }

            // 8. Create demo restaurant orders
            if (restaurantOrderRepository.count() == 0) {
                List<MenuItem> items = menuItemRepository.findAll();
                if (!items.isEmpty()) {
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(0), items.get(3)), "COMPLETED", LocalDateTime.now().minusDays(1));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(1), items.get(4), items.get(7)), "COMPLETED", LocalDateTime.now().minusDays(3));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(2), items.get(5)), "COMPLETED", LocalDateTime.now().minusDays(5));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(3), items.get(10)), "COMPLETED", LocalDateTime.now().minusDays(7));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(0), items.get(6)), "COMPLETED", LocalDateTime.now().minusDays(9));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(8), items.get(11)), "COMPLETED", LocalDateTime.now().minusDays(10));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(4), items.get(9)), "COMPLETED", LocalDateTime.now().minusDays(12));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(1), items.get(3), items.get(7)), "COMPLETED", LocalDateTime.now().minusDays(14));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(0), items.get(2)), "PENDING", LocalDateTime.now().minusHours(2));
                    createOrder(restaurantOrderRepository, Arrays.asList(items.get(3), items.get(8)), "PENDING", LocalDateTime.now().minusHours(4));
                    logger.info("Seeded 10 restaurant orders.");
                }
            }

            // 9. Seed Invoices for beautiful revenue charts
            if (invoiceRepository.count() == 0) {
                List<Reservation> reservations = reservationRepository.findAll();
                List<RestaurantOrder> orders = restaurantOrderRepository.findAll();

                int count = 1;
                for (int i = 0; i < reservations.size(); i++) {
                    Reservation res = reservations.get(i);
                    if ("PAID".equals(res.getPaymentStatus())) {
                        long nights = Math.max(1, ChronoUnit.DAYS.between(res.getCheckInDate(), res.getCheckOutDate()));
                        BigDecimal rent = res.getRoom().getPrice().multiply(BigDecimal.valueOf(nights));
                        createInvoice(invoiceRepository, res, null, rent, InvoiceType.ROOM, LocalDateTime.now().minusDays(3L * i + 1), count++);
                    }
                }

                for (int i = 0; i < Math.min(6, orders.size()); i++) {
                    RestaurantOrder ord = orders.get(i);
                    if ("COMPLETED".equals(ord.getStatus())) {
                        createInvoice(invoiceRepository, null, ord, ord.getTotal(), InvoiceType.RESTAURANT, ord.getCreatedAt(), count++);
                    }
                }
                logger.info("Seeded 10 historical invoices to calculate revenue trends.");
            }

            // 10. Seed API requests logs for telemetry curves
            if (apiLogRepository.count() == 0) {
                String[] endpoints = {"/api/bookings", "/api/bookings", "/api/rooms", "/api/guests", "/api/bookings/1", "/api-dashboard"};
                String[] methods = {"GET", "POST", "GET", "GET", "PUT", "GET"};
                int[] statuses = {200, 201, 200, 200, 200, 200, 400, 500};
                Random rand = new Random();

                for (int i = 0; i < 50; i++) {
                    ApiLog log = new ApiLog();
                    int idx = rand.nextInt(endpoints.length);
                    log.setEndpoint(endpoints[idx]);
                    log.setMethod(methods[idx]);
                    
                    Instant time = Instant.now().minus(rand.nextInt(7), ChronoUnit.DAYS)
                            .minus(rand.nextInt(24), ChronoUnit.HOURS)
                            .minus(rand.nextInt(60), ChronoUnit.MINUTES);
                    log.setRequestTime(time);
                    
                    int statusRoll = rand.nextInt(10);
                    if (statusRoll < 8) {
                        log.setResponseStatus(statuses[rand.nextInt(6)]);
                    } else {
                        log.setResponseStatus(statuses[6 + rand.nextInt(2)]);
                    }

                    log.setDurationMs((long) (15 + rand.nextInt(180)));
                    log.setUsername("admin");
                    apiLogRepository.save(log);
                }
                logger.info("Seeded 50 synthetic API traffic telemetry logs.");
            }

            // 11. Seed Suppliers (Newly Added!)
            if (supplierRepository.count() == 0) {
                createSupplier(supplierRepository, "Luxury Linen Co.", "+1 800 555 0192", "Primary supplier for organic cotton sheets, deluxe bath towels, and pillowcases.");
                createSupplier(supplierRepository, "Gourmet Food Distributors", "+91 99001 12233", "Main vendor supplying fresh vegetables, dairy products, cream, and baking spices.");
                createSupplier(supplierRepository, "Apex Amenities Ltd.", "+91 88002 23344", "Supplies luxury toiletries, organic guest soaps, dental packages, and shower gels.");
                createSupplier(supplierRepository, "Techno Hotel Systems", "+1 888 555 4567", "Supplier for bedroom minibars, smart ambient lights, and digital lockboxes.");
                logger.info("Seeded 4 primary suppliers.");
            }

            // 12. Seed Inventory Items (Newly Added!)
            if (inventoryItemRepository.count() == 0) {
                List<Supplier> suppliers = supplierRepository.findAll();
                if (suppliers.size() >= 4) {
                    createInventoryItem(inventoryItemRepository, "King Cotton Sheets", "Premium 500 thread-count cotton sheets for Presidential and Suite rooms.", 45, suppliers.get(0));
                    createInventoryItem(inventoryItemRepository, "Microfiber Bath Towels", "Super soft absorbent white guest bath towels.", 80, suppliers.get(0));
                    createInventoryItem(inventoryItemRepository, "Luxury Lavender Soaps", "Botanical guest hand soaps (Low stock alert trigger).", 3, suppliers.get(2)); // Triggers alerts
                    createInventoryItem(inventoryItemRepository, "Gourmet Coffee Pods", "Espresso blend capsules for room in-unit coffee machines.", 150, suppliers.get(1));
                    createInventoryItem(inventoryItemRepository, "Premium Dental Kits", "Hotel branded toothbrush and organic toothpaste kits (Low stock alert).", 4, suppliers.get(2)); // Triggers alerts
                    createInventoryItem(inventoryItemRepository, "Smart Keycards", "RFID proximity keycards for room digital locks.", 200, suppliers.get(3));
                    logger.info("Seeded 6 inventory items linking suppliers.");
                }
            }

            // 13. Seed Employees (Newly Added!)
            if (employeeRepository.count() == 0) {
                createEmployee(employeeRepository, "Rajesh Kumar", "Rooms Division", "Front Office Manager", "+91 98123 45678", 45000.0);
                createEmployee(employeeRepository, "Ananya Sen", "Housekeeping", "Executive Housekeeper", "+91 87123 45678", 38000.0);
                createEmployee(employeeRepository, "David Miller", "Culinary Department", "Executive Head Chef", "+1 415 555 9210", 75000.0);
                createEmployee(employeeRepository, "Siddharth Nair", "Security & Maintenance", "Systems Engineer", "+91 76123 45678", 42000.0);
                createEmployee(employeeRepository, "Emily Watson", "Food & Beverage", "Restaurant General Manager", "+44 20 7946 0981", 52000.0);
                createEmployee(employeeRepository, "Neha Sharma", "Finance & Admin", "Accounts Executive", "+91 65123 45678", 35000.0);
                logger.info("Seeded 6 employee profiles.");
            }

            // 14. Seed notifications
            if (notificationRepository.count() == 0) {
                notificationRepository.save(new Notification("Booking confirmation for Room 101 sent.", "BOOKING"));
                notificationRepository.save(new Notification("Payment received for reservation #R-204.", "PAYMENT"));
                notificationRepository.save(new Notification("Inventory alert: Linen stock below threshold.", "INVENTORY"));
                notificationRepository.save(new Notification("Admin review required for room maintenance request.", "MAINTENANCE"));
                logger.info("Seeded 4 notifications.");
            }
        };
    }

    private void createRoom(RoomRepository repo, String num, RoomType type, double price, RoomStatus status, String desc) {
        Room r = new Room();
        r.setRoomNumber(num);
        r.setRoomType(type);
        r.setPrice(BigDecimal.valueOf(price));
        r.setStatus(status);
        r.setDescription(desc);
        repo.save(r);
    }

    private void createGuest(GuestRepository repo, String name, String phone, String email, String address, String idProof) {
        Guest g = new Guest();
        g.setFullName(name);
        g.setPhoneNumber(phone);
        g.setEmail(email);
        g.setAddress(address);
        g.setIdProofNumber(idProof);
        repo.save(g);
    }

    private void createReservation(ReservationRepository repo, Guest guest, Room room, LocalDate in, LocalDate out, int guests, String status) {
        Reservation r = new Reservation();
        r.setGuest(guest);
        r.setRoom(room);
        r.setCheckInDate(in);
        r.setCheckOutDate(out);
        r.setTotalGuests(guests);
        r.setPaymentStatus(status);
        repo.save(r);
    }

    private void createCategory(MenuCategoryRepository repo, String name) {
        MenuCategory c = new MenuCategory();
        c.setName(name);
        repo.save(c);
    }

    private void createMenuItem(MenuItemRepository repo, String name, String desc, double price, MenuCategory cat) {
        MenuItem i = new MenuItem();
        i.setName(name);
        i.setDescription(desc);
        i.setPrice(BigDecimal.valueOf(price));
        i.setCategory(cat);
        repo.save(i);
    }

    private void createOrder(RestaurantOrderRepository repo, List<MenuItem> items, String status, LocalDateTime date) {
        RestaurantOrder o = new RestaurantOrder();
        o.setCreatedAt(date);
        o.setStatus(status);
        
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (MenuItem item : items) {
            OrderItem oi = new OrderItem();
            oi.setOrder(o);
            oi.setMenuItem(item);
            oi.setQuantity(1);
            oi.setPrice(item.getPrice());
            orderItems.add(oi);
            total = total.add(item.getPrice());
        }
        
        o.setItems(orderItems);
        o.setTotal(total);
        repo.save(o);
    }

    private void createInvoice(InvoiceRepository repo, Reservation res, RestaurantOrder ord, BigDecimal subtotal, InvoiceType type, LocalDateTime date, int seq) {
        Invoice inv = new Invoice();
        inv.setInvoiceNumber("INV-" + date.getYear() + String.format("%04d", seq));
        inv.setReservation(res);
        inv.setRestaurantOrder(ord);
        inv.setInvoiceType(type);
        inv.setPaymentMethod(PaymentMethod.CASH);
        inv.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        inv.setTaxAmount(subtotal.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP));
        inv.setTotalAmount(inv.getSubtotal().add(inv.getTaxAmount()).setScale(2, RoundingMode.HALF_UP));
        inv.setCreatedAt(date);
        inv.setNotes("Synthetic seeded transaction log.");
        repo.save(inv);
    }

    private void createSupplier(SupplierRepository repo, String name, String contact, String notes) {
        Supplier s = new Supplier();
        s.setName(name);
        s.setContact(contact);
        s.setNotes(notes);
        repo.save(s);
    }

    private void createInventoryItem(InventoryItemRepository repo, String name, String desc, int quantity, Supplier sup) {
        InventoryItem ii = new InventoryItem();
        ii.setName(name);
        ii.setDescription(desc);
        ii.setQuantity(quantity);
        ii.setSupplier(sup);
        repo.save(ii);
    }

    private void createEmployee(EmployeeRepository repo, String name, String dept, String pos, String phone, double salary) {
        Employee e = new Employee();
        e.setName(name);
        e.setDepartment(dept);
        e.setPosition(pos);
        e.setPhoneNumber(phone);
        e.setSalary(salary);
        repo.save(e);
    }
}
