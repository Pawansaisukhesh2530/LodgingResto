package com.example.lodgingresto.model;

public enum RoomType {
    SINGLE("Single", "fa-bed"),
    DOUBLE("Double", "fa-bed"),
    DELUXE("Deluxe", "fa-crown"),
    SUITE("Suite", "fa-gem"),
    FAMILY("Family", "fa-people-roof"),
    PRESIDENTIAL("Presidential", "fa-star");

    private final String label;
    private final String icon;

    RoomType(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }
}

