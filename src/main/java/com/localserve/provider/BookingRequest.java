package com.localserve.provider;

public class BookingRequest {
    public int id;
    public String userName;
    public String bookingDate;
    public String timeSlot;
    public String status;
    // where the customer needs the service (null for bookings made before location matching)
    public String serviceCity;
    public String serviceState;
    public String servicePinCode;
    public String serviceAddress;

    public BookingRequest(int id, String userName, String bookingDate, String timeSlot, String status,
                          String serviceCity, String serviceState, String servicePinCode, String serviceAddress) {
        this.id = id;
        this.userName = userName;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.status = status;
        this.serviceCity = serviceCity;
        this.serviceState = serviceState;
        this.servicePinCode = servicePinCode;
        this.serviceAddress = serviceAddress;
    }
}
