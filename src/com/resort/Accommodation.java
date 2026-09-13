package com.resort;

abstract class Accommodation {
    private final String TYPE;
    private final boolean PRICE_PER_NIGHT;
    private final int CAPACITY;

    Accommodation(String type, boolean pricePerNight, int capacityPerNight) {
        this.TYPE = type;
        this.PRICE_PER_NIGHT = pricePerNight;
        this.CAPACITY = capacityPerNight;
    }
}
