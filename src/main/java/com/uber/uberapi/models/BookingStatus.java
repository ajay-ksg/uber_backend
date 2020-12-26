package com.uber.uberapi.models;

public enum BookingStatus {
    CANCELED("The Booking has been canceled due to one of many reasons."),
    SCHEDULE("The Booking is Scheduled for future time."),
    ASSIGNING_DRIVER("The passenger has requested for booking, looking for the driver."),
    REACHING_PICKUP_LOCATION("The driver has accepted the booking and driving towards the requested location"),
    CAB_ARRIVED("Reached at source location , waiting for passenger."),
    IN_RIDE("Ride has been started and driving toward destination location."),
    COMPLETED("Reached at destination , Ride has been completed.");

    private final String description;
    BookingStatus(String description){
        this.description = description;
    }
}
