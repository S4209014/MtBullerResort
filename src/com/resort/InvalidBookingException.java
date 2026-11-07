package com.resort;

/**
 * Thrown when user supplied details (customer details, dates, party size, accommodation, etc.)
 * break one of the resort's business rules.
 */
public class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}
