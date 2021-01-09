package com.uber.uberapi.controller;

import com.uber.uberapi.Exceptions.InvalidBookingException;
import com.uber.uberapi.Exceptions.InvalidDriverException;
import com.uber.uberapi.Services.BookingService;
import com.uber.uberapi.Services.Constants;
import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.OTP;
import com.uber.uberapi.models.Review;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/driver")
@RestController
public class DriverController {
    //all the end points that driver will access.
    @Autowired
    DriverRepository driverRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    Constants constants;

    BookingService bookingService;
    /*
    getDriverFromId
    getBookingFromId
    getDriverBookingFromId
    getDriverDetails --> Done
    changeAvailability --> Done
    getAllBookings --> Done
    getBookingFromId -->
    acceptBooking
    cancelBooking
    startRide
    ensRide
    rateRide


    controllers --> services/model
    services--> other services , other controllers, models
    models(DAO) --> DB
    repositories(DAL) --> manages models
     */

    private Driver getDriverFromId(Long driverId){
        Optional<Driver> driver = driverRepository.findById(driverId);
        if(driver.isEmpty())
        {
            throw new InvalidDriverException("No Driver with ID: "+ driverId);
        }

        return driver.get();
    }
    private Booking getBookingFromId(Long bookingId){
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if(booking.isEmpty())
        {
            throw new InvalidBookingException("No Driver with ID: "+ bookingId);
        }

        return booking.get();
    }
    private Booking getDriverBookingFromId(Long bookingId, Long driverId) {
            Booking booking = getBookingFromId(bookingId);
            Driver driver = getDriverFromId(driverId);
            if(!booking.getDriver().equals(driver)){
                throw new InvalidBookingException("Driver " + driverId +" has no booking with Id: "+ bookingId);
            }

            return booking;
    }
    @GetMapping("/{driverId}")
    public Driver getDriverDetails(@PathVariable(name="driverId") Long driverId){
        return getDriverFromId((driverId));
    }

    @PatchMapping("/{driverId}")
    public void changeAvailability(@PathVariable(name="driverId")Long driverId,
                                   @RequestBody Boolean available){

        Driver driver = getDriverFromId(driverId);
        driver.setAvailablity(available);
        driverRepository.save(driver);
    }

    @GetMapping("/{driverId}/bookings")
    public List<Booking> getAllBookings(@PathVariable(name = "driverId")Long driverId ){
        Driver driver  = getDriverFromId(driverId);
        return driver.getBookings();
    }

    @GetMapping("/{driverId}/bookings/{bookingId}")
    public Booking getBookingFromId(@PathVariable(name ="driverId")Long driverId,
                                    @PathVariable(name="bookingId")Long bookingId){
        //driver can only see the bookings that they drive
        return  getDriverBookingFromId(bookingId,driverId);
    }

    @PostMapping("/{driverId}/bookings/{bookingId}")
    public void acceptBooking(@PathVariable(name ="driverId")Long driverId,
                                    @PathVariable(name="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);
        Booking booking =   getDriverBookingFromId(bookingId,driverId);

        //driver can only see the bookings that they drive
        bookingService.acceptBooking(booking,driver);

    }

    @DeleteMapping("/{driverId}/bookings/{bookingId}")
    public void cancelBooking(@PathVariable(name ="driverId")Long driverId,
                              @PathVariable(name="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);
        Booking booking =   getDriverBookingFromId(bookingId,driverId);

        //driver can only see the bookings that they drive
        bookingService.cancelByDriver(booking,driver);
    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/start")
    public void startRide(@PathVariable(name="driverId")Long driverId,
                          @PathVariable(name="bookingId")Long bookingId,
                          @RequestBody OTP otp){

        Booking booking =   getDriverBookingFromId(bookingId,driverId);
        booking.startRide(otp,constants.getRideStartOYPExpiryMinutes());
        bookingRepository.save(booking);
    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/end")
    public void endRide(@PathVariable(name="driverId")Long driverId,
                          @PathVariable(name="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);
        Booking booking =   getDriverBookingFromId(bookingId,driverId);
        booking.endRide();
        driverRepository.save(driver);
        bookingRepository.save(booking);
    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/rate")
    public void rateTheRide(@PathVariable(name="driverId")Long driverId,
                            @PathVariable(name="bookingId")Long bookingId,
                            @RequestBody Review review){

        Booking booking =   getDriverBookingFromId(bookingId,driverId);
        Review reviewByDriver = Review.builder()
                                .ratingOutOfFive(review.getRatingOutOfFive())
                                .note(review.getNote())
                                .build();


        booking.setReviewByDriver(reviewByDriver);
        reviewRepository.save(reviewByDriver);
        bookingRepository.save(booking);
    }
}
