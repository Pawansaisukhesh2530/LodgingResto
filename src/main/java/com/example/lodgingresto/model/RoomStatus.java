package com.example.lodgingresto.model;

public enum RoomStatus {
	AVAILABLE("Available", "badge-available", "fa-circle-check"),
	OCCUPIED("Occupied", "badge-occupied", "fa-user-check"),
	RESERVED("Reserved", "badge-reserved", "fa-calendar-check"),
	MAINTENANCE("Maintenance", "badge-maintenance", "fa-screwdriver-wrench");

	private final String label;
	private final String cssClass;
	private final String icon;

	RoomStatus(String label, String cssClass, String icon) {
		this.label = label;
		this.cssClass = cssClass;
		this.icon = icon;
	}

	public String getLabel() {
		return label;
	}

	public String getCssClass() {
		return cssClass;
	}

	public String getIcon() {
		return icon;
	}
}

