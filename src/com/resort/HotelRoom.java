package com.resort;

public class HotelRoom extends Accommodation {
    public HotelRoom(String id, double pricePerNight) {
        super(id, "Hotel Room (up to 4 people)", 4, pricePerNight);
    }
}
