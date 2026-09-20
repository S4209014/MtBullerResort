package com.resort;

public class Apartment extends Accommodation {
    public Apartment(String id, double pricePerNight) {
        super(id, "Apartment (up to 6 people)", 6, pricePerNight);
    }
}
