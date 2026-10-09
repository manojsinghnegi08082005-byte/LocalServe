package com.localserve.user;

public class Provider {
    public int userId;
    public String service;
    public double pricePerHour;
    public String phone;
    public int experience;
    public String serviceCity;
    public String serviceState;
    public String servicePinCode;

    public Provider(int userId, String service, double pricePerHour, String phone, int experience,
                    String serviceCity, String serviceState, String servicePinCode) {
        this.userId = userId;
        this.service = service;
        this.pricePerHour = pricePerHour;
        this.phone = phone;
        this.experience = experience;
        this.serviceCity = serviceCity;
        this.serviceState = serviceState;
        this.servicePinCode = servicePinCode;
    }
}
