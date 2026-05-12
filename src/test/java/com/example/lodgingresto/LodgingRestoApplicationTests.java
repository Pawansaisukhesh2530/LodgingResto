package com.example.lodgingresto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.lodgingresto.model.RoomStatus;
import com.example.lodgingresto.model.RoomType;

class LodgingRestoApplicationTests {

    @Test
    void applicationLoadsSmokeTest() {
        assertEquals("Available", RoomStatus.AVAILABLE.getLabel());
        assertEquals("Deluxe", RoomType.DELUXE.getLabel());
    }

}
