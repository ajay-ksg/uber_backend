package com.uber.uberapi.Services;

import com.uber.uberapi.Exceptions.InvalidActionForBookingStateException;
import com.uber.uberapi.Services.Notification.NotificationService;
import com.uber.uberapi.Services.messagequeue.MessageQueue;
import com.uber.uberapi.models.*;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    MessageQueue messageQueue;
    @Autowired
    Constants constants;
    @Autowired
    OTPService otpService;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    NotificationService notificationService;
    @Autowired
    DriverRepository driverRepository;

    public void createBooking(Booking booking){
        if(booking.getStartTime().after(new Date())){
            //Schedule for later
             booking.setBookingStatus(BookingStatus.SCHEDULE);
             //producer
             messageQueue.sendMessage(constants.getSchedulingTopicName(),new SchedulingService.Message(booking));
        }
        else{
            //create it now.
            booking.setBookingStatus(BookingStatus.ASSIGNING_DRIVER);
            otpService.sendRideStartOTP(booking.getRideStartOTP());
            messageQueue.sendMessage(constants.getDriverMatchingTopicName(), new DriverMatchingService.Message(booking));

        }
        bookingRepository.save(booking);
        passengerRepository.save(booking.getPassenger());
    }

    public void acceptBooking(Booking booking, Driver driver) {
        if(!booking.needsDriver()){
            notificationService.notify(driver.getPhoneNumber(),"Booking accepted by some other driver");
            return;
        }
        if(!driver.canAcceptBooking(constants.getMaxWaitTimeForPreviousRide()))
        {
            notificationService.notify(driver.getPhoneNumber(),"Cannot accept booking");
            return;
        }

        booking.setDriver(driver);
        driver.setActiveBooking(booking);
        booking.getNotifiedDrivers().clear();
        driver.getAcceptableBookings().clear();

        bookingRepository.save(booking);
        driverRepository.save(driver);
        notificationService.notify(driver.getPhoneNumber(),"Booking accepted");
        notificationService.notify(booking.getPassenger().getPhoneNumber(),"Driver "+driver.getName()+" is arriving at pickup location");

    }

    public void cancelByPassenger(Booking booking, Passenger passenger) {
        try{
            booking.cancel();
            bookingRepository.save(booking);
        }
        catch(InvalidActionForBookingStateException inner){
            notificationService.notify(booking.getPassenger().getPhoneNumber(),"Cannot cancel booking now, please ask your driver to end the ride.");
            throw inner;
        }

    }

    public void cancelByDriver(Booking booking, Driver driver) {
        booking.setDriver(null);
        driver.setActiveBooking(null);
        notificationService.notify(driver.getPhoneNumber(),"Booking has been canceled");
        notificationService.notify(booking.getPassenger().getPhoneNumber(),"Reassigning driver");
        retryBooking(booking);

    }

    public void updateRoute(Booking booking, List<ExactLocation> route) {
        if(!booking.canChangeRoute()){
            throw new InvalidActionForBookingStateException("Ride already has been completed or canceled:  "+ booking.getId());
        }
        booking.setRout(route);
        bookingRepository.save(booking);

    }

    public void retryBooking(Booking booking) {
        createBooking(booking);
    }
}
