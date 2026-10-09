package com.localserve.provider;

public class EarningEntry {
    public int bookingId;
    public String bookingDate;
    public String timeSlot;
    public double amount;

    public EarningEntry(int bookingId, String bookingDate, String timeSlot, double amount) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.amount = amount;
    }
}
