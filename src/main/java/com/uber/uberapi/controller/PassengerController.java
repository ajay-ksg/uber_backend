package com.uber.uberapi.controller;

import com.uber.uberapi.Exceptions.InvalidBookingException;
import com.uber.uberapi.Exceptions.InvalidPassengerException;
import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Passenger;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    BookingRepository bookingRepository;

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
    public Passenger getPassengerDetails(@RequestParam(name = "passengerId")Long passenegerId ){
        return getPassengerFromId(passenegerId);
    }

    @GetMapping("/{passengerId}/bookings")
    public List<Booking> getAllBookings(@RequestParam(name="passengerId")Long passengerId){
        Passenger passenger = getPassengerFromId(passengerId);

        return passenger.getBookings();
    }

    @GetMapping("/{passengerId}/bookings/{bookingId}")
    public Booking getBooking(@RequestParam(name="passengerId")Long passengerId,
                              @RequestParam(name="bookingId")Long bookingId){
        Passenger passenger = getPassengerFromId(passengerId);
        return getPassengerBookingFromId(bookingId,passenger);
    }

    @PutMapping("/{passengerId}/bookings")
    public void requestBooking(@RequestParam(name="passengerId")Long passengerId){

    }
}
