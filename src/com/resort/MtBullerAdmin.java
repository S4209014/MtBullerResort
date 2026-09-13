package com.resort;

import java.util.ArrayList;

public class MtBullerAdmin {
    private boolean FIRST_EXECUTION = false;
    private ArrayList<Accommodation> accomodationOptions = new ArrayList<>();

    public static void main(String[] args) {
        new MtBullerAdmin().init();
    }

    /**
     * Invokes the welcome message for the application, displays necessary information
     * before relaying to the "homepage"
     */
    private void init() {
        setFirstExecutionTrue();
        System.out.println("====================================================");
        System.out.println("Welcome to the Mt Buller Custom Travel Bundle System");
        System.out.println("====================================================");
    }

    private boolean setFirstExecutionTrue() {
        this.FIRST_EXECUTION = true;
        return this.FIRST_EXECUTION;
    }
}