package com.resort;

public class LodgeRoom extends Accommodation {
    public LodgeRoom(String id, double pricePerNight) {
        super(id, "Lodge Room (max 2)", 2, pricePerNight);
    }
}
