package com.uber.uberapi.controller;

import com.uber.uberapi.Exceptions.InvalidBookingException;
import com.uber.uberapi.Exceptions.InvalidPassengerException;
import com.uber.uberapi.Services.BookingService;
import com.uber.uberapi.Services.Notification.NotificationService;
import com.uber.uberapi.models.*;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    NotificationService notificationService;

    /*
    *
    *getPassengerFromId
    * getPassengerBookingFromId
    * getPassengerDetails
    * getAllBookings
    * getBooking
    * requestBooking
    * updateRoute
    * retryBooking
    * cancelBooking
    * rateRide
    *
     */

    private Passenger getPassengerFromId(Long passengerId){
        Optional<Passenger> passenger = passengerRepository.findById(passengerId);
        if(passenger.isEmpty())
        {
            throw new InvalidPassengerException("No such passenger exist with id: "+ passengerId);
        }

        return passenger.get();
    }

    private Booking getPassengerBookingFromId(Long bookingId,Passenger passenger){
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if(optionalBooking.isEmpty()){
            throw new InvalidBookingException("No Such booking exist with Id : "+bookingId);
        }
        Booking  booking  = optionalBooking.get();
        if(!booking.getPassenger().equals(passenger)){
            throw new InvalidBookingException("Passenger: "+passenger.getId() + " has no such booking with Booking id: "+ bookingId);
        }

        return booking;
    }

    @GetMapping("/{passengerId}")
    public Passenger getPassengerDetails(@PathVariable(name = "passengerId")Long passenegerId ){
        return getPassengerFromId(passenegerId);
    }

    @GetMapping("/{passengerId}/bookings")
    public List<Booking> getAllBookings(@PathVariable(name="passengerId")Long passengerId){
        Passenger passenger = getPassengerFromId(passengerId);

        return passenger.getBookings();
    }

    @GetMapping("/{passengerId}/bookings/{bookingId}")
    public Booking getBooking(@PathVariable(name="passengerId")Long passengerId,
                              @PathVariable(name="bookingId")Long bookingId){
        Passenger passenger = getPassengerFromId(passengerId);
        return getPassengerBookingFromId(bookingId,passenger);
    }

    @PutMapping("/{passengerId}/bookings")
    public void requestBooking(@PathVariable(name="passengerId")Long passengerId,
                                @RequestBody Booking data){
        Passenger passenger = getPassengerFromId(passengerId);
        List<ExactLocation> route = new ArrayList<>();
        data.getRout().forEach(exactLocation -> {
            route.add(ExactLocation.builder()
                    .latitude(exactLocation.getLatitude())
                    .longitude(exactLocation.getLongitude())
                    .build());
        });
        Booking booking = new Booking.builder()
                .rideStartOTP(OTP.make(passenger.getPhoneNumber()))
                .rout(route)
                .passenger(passenger)
                .bookingType(data.getBookingType())
                .scheduledTime(data.getScheduledTime())
                .build();

        bookingService.createBooking(booking);

    }

    @DeleteMapping("/{passengerId}/bookings/{bookingId}")
    public void cancelBooking(@PathVariable(name="passengerId")Long passengerId,
                              @PathVariable(name="bookingId")Long bookingId)
    {
        Passenger passenger  = getPassengerFromId(passengerId);
        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        bookingService.cancelByPassenger(booking,passenger);
    }

    @PatchMapping("/{passengerId}/bookings/{bookingId}/rate")
    public void rateRide(@PathVariable(name="passengerId")Long passengerId,
                         @PathVariable(name="bookingId")Long bookingId,
                         @RequestBody Review data){

        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = getPassengerBookingFromId(bookingId,passenger);

        Review review = Review.builder()
                .note(data.getNote())
                .ratingOutOfFive(data.getRatingOutOfFive())
                .build();

        booking.setReviewByUser(review);

        reviewRepository.save(review);
        bookingRepository.save(booking);

    }

    @PatchMapping("{paasengerId/bookings/{bookingId}")
    public void updateRoute(@PathVariable(name="passengerId")Long passengerId,
                            @PathVariable(name="bookingId")Long bookingId,
                            @RequestBody Booking data){

        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        List<ExactLocation> route = new ArrayList<>();
        //add the already visited route
        route.addAll(booking.getCompletedRout());

        //add new route
        data.getRout().forEach(exactLocation -> {
            route.add(ExactLocation.builder()
                    .latitude(exactLocation.getLatitude())
                    .longitude(exactLocation.getLongitude())
                    .build());
        });
        bookingService.updateRoute(booking,route);
        notificationService.notify(booking.getDriver().getPhoneNumber(), "Route has been Updated");

    }

    @PostMapping("/{passengerId}/bookings/{b0okingId}")
    public void retryBooking(@PathVariable(name="passengerId")Long passengerId,
                             @PathVariable(name="bookingId")Long bookingId){
        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        bookingService.retryBooking(booking);

    }
}
