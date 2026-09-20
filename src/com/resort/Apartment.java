package com.resort;

public class Apartment extends Accommodation {
    public Apartment(String id, double pricePerNight) {
        super(id, "Apartment (max 6)", 6, pricePerNight);
    }
}
