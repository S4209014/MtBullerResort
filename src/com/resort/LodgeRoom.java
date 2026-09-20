package com.resort;

public class LodgeRoom extends Accommodation {
    public LodgeRoom(String id, double pricePerNight) {
        super(id, "Lodge Room (up to 2 people)", 2, pricePerNight);
    }
}
