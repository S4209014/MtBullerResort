package com.resort;

public class HotelRoom extends Accommodation {
    public HotelRoom(String id, double pricePerNight) {
        super(id, "Hotel Room (max 4)", 4, pricePerNight);
    }
}
