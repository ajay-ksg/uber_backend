package com.uber.uberapi.models;

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
    private  OTP otp;

    private Long totalDistanceMeters;

}
