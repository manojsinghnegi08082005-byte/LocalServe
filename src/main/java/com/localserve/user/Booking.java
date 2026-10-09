package com.localserve.user;

public class Booking {
    public int id;
    public String service;
    public String bookingDate;
    public String timeSlot;
    public String status;
    // location saved with the booking (null for bookings made before location matching)
    public String serviceCity;
    public String serviceState;
    public String servicePinCode;
    public String serviceAddress;

    public Booking(int id, String service, String bookingDate, String timeSlot, String status,
                   String serviceCity, String serviceState, String servicePinCode, String serviceAddress) {
        this.id = id;
        this.service = service;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.status = status;
        this.serviceCity = serviceCity;
        this.serviceState = serviceState;
        this.servicePinCode = servicePinCode;
        this.serviceAddress = serviceAddress;
    }
}
