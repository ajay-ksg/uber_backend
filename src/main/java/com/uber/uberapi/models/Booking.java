package com.uber.uberapi.models;

import com.uber.uberapi.Exceptions.InvalidActionForBookingStateException;
import com.uber.uberapi.Exceptions.InvalidOTPException;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking", indexes = {
        @Index(columnList = "passenger_id"),
        @Index(columnList = "driver_id"),
})

public class Booking  extends  Auditable{
    @ManyToOne
    private Passenger passenger;

    @ManyToOne
    private Driver driver;

    @Enumerated(value = EnumType.STRING)
    private BookingType bookingType;

    @Enumerated(value = EnumType.STRING)
    private  BookingStatus bookingStatus;

    @OneToOne
    private Review reviewByUser;

    @OneToOne
    private Review reviewByDriver;

    @OneToOne
    private PaymentReceipt paymentReceipt;

    @OneToMany
    List<ExactLocation> rout = new ArrayList<>();

    @Temporal(value= TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(value= TemporalType.TIMESTAMP)
    private Date EndTime;

    @OneToOne
    private  OTP rideStartOTP;

    private Long totalDistanceMeters;

    public void startRide(OTP otp) {
        if(!bookingStatus.equals(BookingStatus.CAB_ARRIVED))
        {
            throw new InvalidActionForBookingStateException("Cannot start the Ride as Cab is not arrived yet.");
        }

        if(!rideStartOTP.ValidteEnteredOTP(otp,constants.RIDE_START_OTP_EXPIRY_MINUTES)){
            throw new InvalidOTPException();
        }

        bookingStatus = BookingStatus.IN_RIDE;
    }

    public void endRide() {
        if(!bookingStatus.equals(BookingStatus.IN_RIDE))
        {
            throw new InvalidActionForBookingStateException("Cannot end the Ride as doesn't started yet.");
        }

        bookingStatus = BookingStatus.COMPLETED;
    }
}
