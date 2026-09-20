package com.resort;

public class DateInPastException extends RuntimeException {
    public DateInPastException(String message) {
        super(message);
    }
}
