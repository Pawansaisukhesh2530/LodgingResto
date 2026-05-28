package com.example.lodgingresto.service;

import com.example.lodgingresto.model.*;
import com.example.lodgingresto.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroqServiceImpl implements GroqService {

    private static final Logger logger = LoggerFactory.getLogger(GroqServiceImpl.class);

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String modelName;

    private final RoomRepository roomRepository;
    private final MenuItemRepository menuItemRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final AiRequestHistoryRepository aiRequestHistoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GroqServiceImpl(RoomRepository roomRepository,
                           MenuItemRepository menuItemRepository,
                           ReservationRepository reservationRepository,
                           GuestRepository guestRepository,
                           AiRequestHistoryRepository aiRequestHistoryRepository) {
        this.roomRepository = roomRepository;
        this.menuItemRepository = menuItemRepository;
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.aiRequestHistoryRepository = aiRequestHistoryRepository;
    }

    @Override
    public String chat(String message, String username) {
        Instant startTime = Instant.now();
        String category = detectCategory(message);
        String responseContent;

        // Try Calling Groq AI
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            try {
                responseContent = callGroqApi(message, category);
                logger.info("Successfully fetched response from Groq AI.");
            } catch (Exception ex) {
                logger.error("Failed to connect to Groq AI. Switching to Local NLP Fallback Engine.", ex);
                responseContent = runLocalFallback(message, category);
            }
        } else {
            logger.info("No Groq API Key found. Using Local NLP Fallback Engine.");
            responseContent = runLocalFallback(message, category);
        }

        // Save AI Transaction in Database History
        try {
            AiRequestHistory history = new AiRequestHistory();
            history.setPrompt(message);
            history.setResponse(responseContent);
            history.setTimestamp(Instant.now());
            history.setUsername(username == null ? "anonymous" : username);
            history.setCategory(category);
            aiRequestHistoryRepository.save(history);
        } catch (Exception ex) {
            logger.error("Failed to save AI request history log.", ex);
        }

        return responseContent;
    }

    private String detectCategory(String message) {
        String msg = message.toLowerCase();
        if (msg.contains("room") || msg.contains("stay") || msg.contains("suite") || msg.contains("deluxe") || msg.contains("presidential") || msg.contains("romantic") || msg.contains("luxury")) {
            return "ROOM";
        } else if (msg.contains("food") || msg.contains("menu") || msg.contains("dinner") || msg.contains("lunch") || msg.contains("breakfast") || msg.contains("vegetarian") || msg.contains("spicy") || msg.contains("dessert") || msg.contains("eat") || msg.contains("dish")) {
            return "FOOD";
        } else if (msg.contains("book") || msg.contains("reservation") || msg.contains("check-in") || msg.contains("check-out") || msg.contains("stay dates")) {
            return "BOOKING";
        }
        return "GENERAL";
    }

    private String callGroqApi(String message, String category) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // 1. Fetch Real Database Context to Engineer the Prompts
        String dbContext = buildSystemContext();

        // 2. Prepare System Instruction
        String systemPrompt = "You are the 'AI Smart Hotel & Restaurant Assistant' for the premium 'Lodgings Resto' Hotel. " +
                "Your role is to assist guests and hotel operators professionally, politely, and effectively. " +
                "You must strictly use the real-time database context provided below to formulate your answers. " +
                "Highlight actual room numbers, rates, and dishes that exist in our database. Do not hallucinate items that do not exist.\n\n" +
                "Formatting Guidelines:\n" +
                "- Respond using clean, beautifully formatted Markdown.\n" +
                "- Use rich tables, structured bullet points, and bold text for recommended items.\n" +
                "- Feel customer-friendly, professional, and luxurious.\n\n" +
                "[REAL-TIME DATABASE CONTEXT]:\n" + dbContext;

        // 3. Construct Jackson JSON Request Node
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", modelName);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1200);

        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode systemMessage = objectMapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        ObjectNode userMessageNode = objectMapper.createObjectNode();
        userMessageNode.put("role", "user");
        userMessageNode.put("content", message);
        messages.add(userMessageNode);

        requestBody.set("messages", messages);

        String jsonPayload = objectMapper.writeValueAsString(requestBody);

        // 4. Dispatch REST HTTP Post Request to Groq URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Groq API returned HTTP error: " + response.statusCode() + " -> " + response.body());
        }

        // 5. Parse JSON Response
        JsonNode root = objectMapper.readTree(response.body());
        return root.path("choices").get(0).path("message").path("content").asText();
    }

    private String buildSystemContext() {
        StringBuilder sb = new StringBuilder();

        // Include Room Context
        List<Room> rooms = roomRepository.findAll();
        sb.append("--- ROOMS INVENTORY ---\n");
        if (rooms.isEmpty()) {
            sb.append("No room records available currently.\n");
        } else {
            for (Room r : rooms) {
                sb.append(String.format("- Room %s: Type=%s, Price=INR %.2f, Status=%s, Description=%s\n",
                        r.getRoomNumber(), r.getRoomType().getLabel(), r.getPrice(), r.getStatus().getLabel(),
                        r.getDescription() == null ? "None" : r.getDescription()));
            }
        }

        // Include Restaurant Menu Context
        List<MenuItem> menuItems = menuItemRepository.findAll();
        sb.append("\n--- RESTAURANT MENU ITEMS ---\n");
        if (menuItems.isEmpty()) {
            sb.append("No menu items available currently.\n");
        } else {
            for (MenuItem m : menuItems) {
                sb.append(String.format("- %s: Price=INR %.2f, Category=%s, Description=%s\n",
                        m.getName(), m.getPrice(), m.getCategory() == null ? "General" : m.getCategory().getName(),
                        m.getDescription() == null ? "None" : m.getDescription()));
            }
        }

        // Include Operational Statistics
        long totalR = rooms.size();
        long availableR = rooms.stream().filter(r -> r.getStatus() == RoomStatus.AVAILABLE).count();
        long occupiedR = rooms.stream().filter(r -> r.getStatus() == RoomStatus.OCCUPIED).count();
        long totalReservations = reservationRepository.count();
        long totalGuests = guestRepository.count();

        sb.append("\n--- SYSTEM STATISTICS ---\n");
        sb.append("Total Registered Rooms: ").append(totalR).append("\n");
        sb.append("Available Rooms for Booking: ").append(availableR).append("\n");
        sb.append("Occupied Rooms: ").append(occupiedR).append("\n");
        sb.append("Total Reservations Logged: ").append(totalReservations).append("\n");
        sb.append("Total Registered Guests: ").append(totalGuests).append("\n");

        return sb.toString();
    }

    private String runLocalFallback(String message, String category) {
        StringBuilder sb = new StringBuilder();
        sb.append("### 🌟 AI Smart Assistant (Local Database Context Enabled)\n\n");
        sb.append("Greetings! I am your **Smart Lodgings & Restaurant Assistant**. I have connected directly to our database to give you accurate real-time recommendations.\n\n");

        if ("ROOM".equalsIgnoreCase(category)) {
            sb.append("Based on our available rooms database, here are our recommended rooms:\n\n");
            List<Room> rooms = roomRepository.findAll();
            
            // Check budget constraints in prompt
            BigDecimal budget = extractBudget(message);
            List<Room> candidates = rooms;
            if (budget != null) {
                sb.append(String.format("🔍 *Filtering for rooms under budget: **INR %,.2f***\n\n", budget));
                candidates = rooms.stream().filter(r -> r.getPrice().compareTo(budget) <= 0).collect(Collectors.toList());
            }

            // Check features/categories in prompt
            String msg = message.toLowerCase();
            if (msg.contains("romantic") || msg.contains("luxury") || msg.contains("premium")) {
                candidates = candidates.stream().filter(r -> r.getRoomType() == RoomType.DELUXE || r.getRoomType() == RoomType.SUITE || r.getRoomType() == RoomType.PRESIDENTIAL).collect(Collectors.toList());
                sb.append("✨ *Recommending our **Deluxe, Suite & Presidential** configurations:*\n\n");
            } else if (msg.contains("family") || msg.contains("kids")) {
                candidates = candidates.stream().filter(r -> r.getRoomType() == RoomType.FAMILY || r.getRoomType() == RoomType.SUITE || r.getRoomType() == RoomType.DOUBLE).collect(Collectors.toList());
                sb.append("👨‍👩‍👧‍👦 *Recommending family-friendly room setups:*\n\n");
            }

            if (candidates.isEmpty()) {
                sb.append("⚠️ We don't have rooms matching that exact combination. However, here are our overall room options:\n\n");
                candidates = rooms.stream().limit(5).collect(Collectors.toList());
            }

            sb.append("| Room # | Room Type | Price (Night) | Status | Key Features |\n");
            sb.append("| :--- | :--- | :--- | :--- | :--- |\n");
            for (Room r : candidates) {
                sb.append(String.format("| **Room %s** | %s | INR %,.2f | `%s` | %s |\n",
                        r.getRoomNumber(), r.getRoomType().getLabel(), r.getPrice(), r.getStatus().getLabel(),
                        r.getDescription() != null ? r.getDescription() : "Spacious and elegant room layout"));
            }

            sb.append("\n💡 *To book any room instantly, navigate to our [Reservations Board](file:///rooms) or contact our reception.*");

        } else if ("FOOD".equalsIgnoreCase(category)) {
            sb.append("Here are our chef's recommended menu items matching your preferences:\n\n");
            List<MenuItem> items = menuItemRepository.findAll();
            String msg = message.toLowerCase();

            List<MenuItem> candidates = items;
            if (msg.contains("vegetarian") || msg.contains("veg")) {
                sb.append("🌱 *Displaying fine vegetarian options:*\n\n");
                candidates = items.stream().filter(i -> {
                    String desc = i.getDescription() != null ? i.getDescription().toLowerCase() : "";
                    String name = i.getName().toLowerCase();
                    return name.contains("paneer") || name.contains("veg") || name.contains("salad") || name.contains("dal") || name.contains("roti") || desc.contains("veg") || desc.contains("vegetarian") || i.getCategory() != null && i.getCategory().getName().toLowerCase().contains("starter");
                }).collect(Collectors.toList());
            } else if (msg.contains("dessert") || msg.contains("sweet") || msg.contains("kids")) {
                sb.append("🍫 *Displaying gourmet desserts and sweet delicacies:*\n\n");
                candidates = items.stream().filter(i -> i.getCategory() != null && i.getCategory().getName().toLowerCase().contains("dessert") || i.getName().toLowerCase().contains("cake") || i.getName().toLowerCase().contains("brownie") || i.getName().toLowerCase().contains("sweet")).collect(Collectors.toList());
            } else if (msg.contains("spicy")) {
                sb.append("🌶️ *Displaying spicy and aromatic dishes:*\n\n");
                candidates = items.stream().filter(i -> i.getName().toLowerCase().contains("tikka") || i.getName().toLowerCase().contains("chilli") || i.getName().toLowerCase().contains("chicken") || i.getName().toLowerCase().contains("curry")).collect(Collectors.toList());
            }

            if (candidates.isEmpty()) {
                candidates = items.stream().limit(5).collect(Collectors.toList());
            }

            sb.append("| Dish Name | Category | Price | Description |\n");
            sb.append("| :--- | :--- | :--- | :--- |\n");
            for (MenuItem i : candidates) {
                sb.append(String.format("| **%s** | *%s* | INR %,.2f | %s |\n",
                        i.getName(), i.getCategory() != null ? i.getCategory().getName() : "Mains", i.getPrice(),
                        i.getDescription() != null ? i.getDescription() : "Traditional recipe cooked to perfection."));
            }

            sb.append("\n🍽️ *Visit our [Restaurant Menu Console](file:///restaurant) to place an order or call room service!*");

        } else if ("BOOKING".equalsIgnoreCase(category)) {
            long totalR = roomRepository.count();
            long availableR = roomRepository.findByRoomTypeAndStatus(RoomType.SINGLE, RoomStatus.AVAILABLE).size()
                    + roomRepository.findByRoomTypeAndStatus(RoomType.DOUBLE, RoomStatus.AVAILABLE).size()
                    + roomRepository.findByRoomTypeAndStatus(RoomType.DELUXE, RoomStatus.AVAILABLE).size();

            sb.append("### 📅 Live Booking suggestions & System Load\n\n");
            sb.append(String.format("1. **Room Inventory Capacity:** We have **%d total rooms** registered on our grid.\n", totalR));
            sb.append(String.format("2. **Immediate Availability:** **%d rooms** are available for immediate check-in right now.\n", availableR));
            sb.append("3. **Stay Recommendations:** For couples, we recommend checking in to our *Deluxe or Suite Room* configuration. For large group configurations, we suggest booking *Family Rooms* which feature multi-bed setups.\n\n");
            sb.append("To book a stay:\n");
            sb.append("- Go to the **Reservations Module** tab in your sidebar.\n");
            sb.append("- Click **New Booking / Add Reservation**.\n");
            sb.append("- Fill in the guest identity records, pick an Available room number, and save.\n");

        } else {
            sb.append("How can I assist you with your operations today? Here are some quick topics I can address:\n\n");
            sb.append("1. 🏨 **Room Inquiries:** Ask me to suggest rooms based on your budget (e.g. *\"Suggest a room under INR 5000\"*).\n");
            sb.append("2. 🍛 **Food Inquiries:** Ask me to recommend dishes based on taste or diet (e.g. *\"Suggest vegetarian dinner ideas\"*).\n");
            sb.append("3. 📈 **System Telemetry:** Ask me about live room status statistics and reservation percentages.\n\n");
            sb.append("Let me know how I can guide your stay or hospitality workflow!");
        }

        return sb.toString();
    }

    private BigDecimal extractBudget(String message) {
        String cleaned = message.replaceAll("[^0-9]", " ").trim();
        String[] tokens = cleaned.split("\\s+");
        for (String t : tokens) {
            if (!t.isEmpty()) {
                try {
                    double val = Double.parseDouble(t);
                    if (val > 100) { // filter out small numbers like count
                        return BigDecimal.valueOf(val);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }
}
