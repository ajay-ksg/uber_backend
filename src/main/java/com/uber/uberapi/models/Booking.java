package com.uber.uberapi.models;

import com.uber.uberapi.Exceptions.InvalidActionForBookingStateException;
import com.uber.uberapi.Exceptions.InvalidOTPException;
import lombok.*;

import javax.persistence.*;
import java.awt.print.Book;
import java.util.*;

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

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Driver> notifiedDrivers = new HashSet<>();

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "booking_route",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "exact_location_id"),
            indexes = {@Index(columnList = "booking_id")}
    )
    @OrderColumn(name = "location_index")
    private List<ExactLocation> rout = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "booking_completed_route",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "exact_location_id"),
            indexes = {@Index(columnList = "booking_id")}
    )
    @OrderColumn(name = "location_index")
    private List<ExactLocation> completedRout = new ArrayList<>();

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date scheduledTime;

    @Temporal(value= TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(value= TemporalType.TIMESTAMP)
    private Date EndTime;

    @Temporal(value= TemporalType.TIMESTAMP)
    private Date expectedCompletionTime;


    @OneToOne
    private  OTP rideStartOTP;

    private Long totalDistanceMeters;

    public void startRide(OTP otp, Integer RIDE_START_OTP_EXPIRY_MINUTES) {
        if(!bookingStatus.equals(BookingStatus.CAB_ARRIVED))
        {
            throw new InvalidActionForBookingStateException("Cannot start the Ride as Cab is not arrived yet.");
        }

        if(!rideStartOTP.ValidteEnteredOTP(otp,RIDE_START_OTP_EXPIRY_MINUTES)){
            throw new InvalidOTPException();
        }

        bookingStatus = BookingStatus.IN_RIDE;
    }

    public void endRide() {
        if(!bookingStatus.equals(BookingStatus.IN_RIDE))
        {
            throw new InvalidActionForBookingStateException("Cannot end the Ride as doesn't started yet.");
        }
        driver.setActiveBooking(null);
        bookingStatus = BookingStatus.COMPLETED;
    }

    public boolean canChangeRoute() {
        return (bookingStatus.equals(BookingStatus.IN_RIDE)
                ||bookingStatus.equals(BookingStatus.CAB_ARRIVED)
                ||bookingStatus.equals(BookingStatus.ASSIGNING_DRIVER)
                ||bookingStatus.equals(BookingStatus.REACHING_PICKUP_LOCATION)
                ||bookingStatus.equals(BookingStatus.SCHEDULE)
        );
    }

    public boolean needsDriver() {
        return bookingStatus.equals(BookingStatus.ASSIGNING_DRIVER);
    }

    public ExactLocation getPickUpLocation(){
        return rout.get(0);
    }

    public void cancel() {
        if(!(bookingStatus.equals(BookingStatus.ASSIGNING_DRIVER)
                || bookingStatus.equals(BookingStatus.SCHEDULE)
                ||bookingStatus.equals(BookingStatus.REACHING_PICKUP_LOCATION)
                ||bookingStatus.equals(BookingStatus.CAB_ARRIVED)))
        {
            throw new InvalidActionForBookingStateException("Cannot cancel the In-progress ride, ask you driver to end the ride.");
        }

        bookingStatus = BookingStatus.CANCELED;
        notifiedDrivers.clear();
        driver = null;

    }


    public static class builder{
        private Passenger passenger;
        private Driver driver;
        private BookingType bookingType;
        private  BookingStatus bookingStatus;
        private Review reviewByUser;
        private Review reviewByDriver;
        private PaymentReceipt paymentReceipt;
        List<ExactLocation> rout = new ArrayList<>();
        @Temporal(value = TemporalType.TIMESTAMP)
        private Date startTime;

        @Temporal(value = TemporalType.TIMESTAMP)
        private Date endTime;

        @Temporal(value = TemporalType.TIMESTAMP)
        private Date scheduledTime;

        private  OTP rideStartOTP;
        private Long totalDistanceMeters;


       public Booking build() {
            Booking booking = new Booking();
            booking.setPassenger(this.passenger);
            booking.setDriver(this.driver);
            booking.setBookingType(this.bookingType);
            booking.setBookingStatus(this.bookingStatus);
            booking.setReviewByDriver(this.reviewByDriver);
            booking.setEndTime(this.endTime);
            booking.setStartTime(this.startTime);
            booking.setRideStartOTP(this.rideStartOTP);
            booking.setReviewByUser(this.reviewByUser);
            booking.setPaymentReceipt(this.paymentReceipt);
            booking.setRout(this.rout);
            booking.setTotalDistanceMeters(this.totalDistanceMeters);

            return booking;

        }
        public builder passenger(Passenger passenger){
                this.passenger = passenger;
                return this;
        }
        public builder driver(Driver driver)
        {
            this.driver = driver;
            return this;
        }
        public builder bookingType(BookingType bookingType)
        {
            this.bookingType = bookingType;
            return this;
        }
        public builder bookingStatus(BookingStatus bookingStatus)
        {
            this.bookingStatus = bookingStatus;
            return this;
        }
        public builder rout(List<ExactLocation> rout)
        {
            this.rout = rout;
            return this;
        }
        public builder reviewByDriver(Review reviewByDriver)
        {
            this.reviewByDriver = reviewByDriver;
            return this;
        }
        public builder reviewByUser(Review reviewByDriver)
        {
            this.reviewByDriver = reviewByDriver;
            return this;
        }
        public builder startTime(Date startTime)
        {
            this.startTime = startTime;
            return this;
        }
        public builder endTime(Date endTime)
        {
            this.endTime = endTime;
            return this;
        }
        public builder paymentReceipt(PaymentReceipt paymentReceipt)
        {
            this.paymentReceipt = paymentReceipt;
            return this;
        }
        public builder totalDistanceMeters(Long totalDistanceMeters)
        {
            this.totalDistanceMeters = totalDistanceMeters;
            return this;
        }
        public builder rideStartOTP(OTP rideStartOTP)
        {
            this.rideStartOTP = rideStartOTP;
            return this;
        }
        public  builder scheduledTime(Date scheduledTime){
           this.scheduledTime = scheduledTime;
           return this;
        }
    };
}
